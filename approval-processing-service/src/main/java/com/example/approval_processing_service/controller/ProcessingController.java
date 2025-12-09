package com.example.approval_processing_service.controller;

import com.example.approval_processing_service.grpc.ApprovalRequest;
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
    public ResponseEntity<List<ApprovalRequest>> getQueue(@PathVariable int approverId) {
        return ResponseEntity.ok(queue.getQueue(approverId));
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
