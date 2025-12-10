package com.example.approval_processing_service.controller;

import com.example.approval.grpc.ApprovalRequest;
import com.example.approval_processing_service.dto.ProcessQueueDto;
import com.example.approval_processing_service.service.ProcessingDecisionService;
import com.example.approval_processing_service.service.ProcessingQueueService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class ProcessingController {

    private final ProcessingQueueService queue;
    private final ProcessingDecisionService decisionService;

    @GetMapping("/process/{approverId}")
    public ResponseEntity<List<ProcessQueueDto>> getQueue(@PathVariable int approverId) {
        List<ApprovalRequest> list = queue.getQueue(approverId);

        List<ProcessQueueDto> dtoList = list.stream()
                .map(req -> new ProcessQueueDto(
                        req.getRequestId(),
                        req.getRequesterId(),
                        req.getTitle(),
                        req.getContent()
                ))
                .toList();

        return ResponseEntity.ok(dtoList);
    }

    public record DecisionRequest(String status) {}

    @PostMapping("/process/{approverId}/{requestId}")
    public ResponseEntity<Void> decide(
            @PathVariable int approverId,
            @PathVariable int requestId,
            @RequestBody DecisionRequest body
    ) {
        boolean ok = decisionService.decide(approverId, requestId, body.status());
        return ok ? ResponseEntity.ok().build() : ResponseEntity.notFound().build();
    }
}
