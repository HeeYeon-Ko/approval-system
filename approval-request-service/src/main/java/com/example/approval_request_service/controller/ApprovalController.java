package com.example.approval_request_service.controller;

import com.example.approval_request_service.dto.req.CreateApprovalRequest;
import com.example.approval_request_service.dto.res.ApprovalResponseDto;
import com.example.approval_request_service.dto.res.RequestIdResponse;
import com.example.approval_request_service.service.ApprovalService;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ApprovalController {

    private final ApprovalService approvalService;

    // 결재 요청 생성
    @PostMapping("/approvals")
    public ResponseEntity<RequestIdResponse> createApproval(@RequestBody @Valid CreateApprovalRequest request) {
        Long requestId = approvalService.createApproval(request);
        return ResponseEntity.ok(new RequestIdResponse(requestId));
    }

    // 결재 요청 목록 조회
    @GetMapping("/approvals")
    public ResponseEntity<List<ApprovalResponseDto>> getApprovals() {
        return ResponseEntity.ok(approvalService.getApprovals());
    }

    // 결재 요청 상세 조회
    @GetMapping("/approvals/{requestId}")
    public ResponseEntity<ApprovalResponseDto> getApproval(@PathVariable Long requestId) {
        return ResponseEntity.ok(approvalService.getApproval(requestId));
    }
}
