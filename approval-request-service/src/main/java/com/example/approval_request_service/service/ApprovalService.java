package com.example.approval_request_service.service;

import com.example.approval_request_service.client.ApprovalProcessingClient;
import com.example.approval_request_service.client.NotificationClient;
import com.example.approval_request_service.domain.ApprovalDocument;
import com.example.approval_request_service.domain.Step;
import com.example.approval_request_service.dto.req.CreateApprovalRequest;
import com.example.approval_request_service.dto.req.NotificationRequest;
import com.example.approval_request_service.dto.res.ApprovalResponseDto;
import com.example.approval_request_service.repository.ApprovalRepository;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.approval_request_service.client.EmployeeClient;

@Service
@RequiredArgsConstructor
public class ApprovalService {

    private final ApprovalRepository approvalRepository;
    private final EmployeeClient employeeClient;
    private final ApprovalProcessingClient approvalProcessingClient;
    private final NotificationClient notificationClient;

    // 결재 요청 생성
    @Transactional
    public Long createApproval(CreateApprovalRequest request) {

        // Employee Service로 requesterId/approverId 검증
        employeeClient.validateEmployeeExists(request.requesterId());
        request.steps().forEach(step ->
                employeeClient.validateEmployeeExists(step.approverId())
        );

        // steps 오름차순 검증
        validateStepsOrder(request.steps());

        Long nextRequestId = generateNextRequestId();
        Instant now = Instant.now();

        // DTO -> Step 리스트
        List<Step> steps = request.steps().stream()
                .sorted(Comparator.comparing(CreateApprovalRequest.StepRequest::step))
                .map(s -> Step.builder()
                        .step(s.step())
                        .approverId(s.approverId())
                        .status("pending")
                        .updatedAt(null)  // 승인 전
                        .build())
                .collect(Collectors.toList());

        ApprovalDocument document = ApprovalDocument.builder()
                .requestId(nextRequestId)
                .requesterId(request.requesterId())
                .title(request.title())
                .content(request.content())
                .steps(steps)  // Step 리스트
                .finalStatus("in_progress")
                .createdAt(now)
                .updatedAt(now)
                .build();

        approvalRepository.save(document);

        // MongoDB에 저장이 끝난 후 gRPC로 Processing Service에 전달
        approvalProcessingClient.sendApprovalRequest(
                nextRequestId,
                request.requesterId(),
                request.title(),
                request.content(),
                steps
        );

        return nextRequestId;
    }

    // 결재 요청 목록 조회
    @Transactional(readOnly = true)
    public List<ApprovalResponseDto> getApprovals() {
        return approvalRepository.findAll().stream()
                .map(this::toResponseDto)
                .collect(Collectors.toList());
    }

    // 결재 요청 상세 조회
    @Transactional(readOnly = true)
    public ApprovalResponseDto getApproval(Long requestId) {
        ApprovalDocument document = approvalRepository.findByRequestId(requestId)
                .orElseThrow(() -> new IllegalArgumentException("Approval not found"));
        return toResponseDto(document);
    }

    // 결재 요청 번호
    private Long generateNextRequestId() {
        return approvalRepository.findTopByOrderByRequestIdDesc()
                .map(doc -> doc.getRequestId() + 1)
                .orElse(1L);
    }

    // steps 오름차순 검증
    private void validateStepsOrder(List<CreateApprovalRequest.StepRequest> steps) {
        if (steps == null || steps.isEmpty()) {
            throw new IllegalArgumentException("Steps must not be empty");
        }
        List<Integer> sortedSteps = steps.stream()
                .map(CreateApprovalRequest.StepRequest::step)
                .sorted()
                .toList();

        int expected = 1;
        for (Integer s : sortedSteps) {
            if (!s.equals(expected)) {
                throw new IllegalArgumentException("Steps must start from 1 and be consecutive");
            }
            expected++;
        }
    }

    private ApprovalResponseDto toResponseDto(ApprovalDocument doc) {
        List<ApprovalResponseDto.StepResponse> steps = doc.getSteps().stream()
                .map(s -> new ApprovalResponseDto.StepResponse(
                        s.getStep(),
                        s.getApproverId(),
                        s.getStatus(),
                        s.getUpdatedAt()
                ))
                .collect(Collectors.toList());

        return new ApprovalResponseDto(
                doc.getRequestId(),
                doc.getRequesterId(),
                doc.getTitle(),
                doc.getContent(),
                doc.getFinalStatus(),
                doc.getCreatedAt(),
                doc.getUpdatedAt(),
                steps
        );
    }

    @Transactional
    public void handleApprovalResult(int requestId, int step, int approverId, String status) {
        ApprovalDocument doc = approvalRepository.findByRequestId((long) requestId)
                .orElseThrow(() -> new IllegalArgumentException("Approval not found"));

        Instant now = Instant.now();

        List<Step> updatedSteps = doc.getSteps().stream()
                .map(s -> {
                    if (s.getStep().equals(step) && s.getApproverId().equals((long) approverId)) {
                        return Step.builder()
                                .step(s.getStep())
                                .approverId(s.getApproverId())
                                .status(status)
                                .updatedAt(now)
                                .build();
                    }
                    return s;
                })
                .toList();

        String finalStatus = doc.getFinalStatus();
        if ("rejected".equals(status)) {
            finalStatus = "rejected";
        } else if ("approved".equals(status)) {
            boolean allApproved = updatedSteps.stream()
                    .allMatch(s -> "approved".equals(s.getStatus()));
            if (allApproved) {
                finalStatus = "approved";
            } else {
                finalStatus = "in_progress";
            }
        }

        ApprovalDocument updated = ApprovalDocument.builder()
                .id(doc.getId())
                .requestId(doc.getRequestId())
                .requesterId(doc.getRequesterId())
                .title(doc.getTitle())
                .content(doc.getContent())
                .steps(updatedSteps)
                .finalStatus(finalStatus)
                .createdAt(doc.getCreatedAt())
                .updatedAt(now)
                .build();

        approvalRepository.save(updated);

        if ("approved".equals(finalStatus)) {
            NotificationRequest notification = new NotificationRequest(
                    updated.getRequestId(),
                    updated.getRequesterId(),
                    "approved",
                    "approved",
                    null
            );
            notificationClient.sendNotification(notification);
        } else if ("rejected".equals(finalStatus)) {
            NotificationRequest notification = new NotificationRequest(
                    updated.getRequestId(),
                    updated.getRequesterId(),
                    "rejected",
                    "rejected",
                    (long) approverId
            );
            notificationClient.sendNotification(notification);
        } else if ("approved".equals(status) && "in_progress".equals(finalStatus)) {
            approvalProcessingClient.sendApprovalRequest(
                    updated.getRequestId(),
                    updated.getRequesterId(),
                    updated.getTitle(),
                    updated.getContent(),
                    updated.getSteps()
            );
        }
    }
}
