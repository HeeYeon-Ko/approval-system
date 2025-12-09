package com.example.approval_processing_service.service;

import com.example.approval_processing_service.client.ApprovalRequestClient;
import com.example.approval_processing_service.grpc.ApprovalRequest;
import com.example.approval_processing_service.grpc.Step;
import org.springframework.stereotype.Service;

@Service
public class ProcessingDecisionService {
    private final ProcessingQueueService queue;
    private final ApprovalRequestClient requestClient;

    public ProcessingDecisionService(
            ProcessingQueueService queue,
            ApprovalRequestClient requestClient
    ) {
        this.queue = queue;
        this.requestClient = requestClient;
    }

    public boolean decide(int approverId, int requestId, String status) {
        ApprovalRequest req = queue.pop(approverId, requestId);
        if (req == null) return false;

        Step step = req.getStepsList().stream()
                .filter(s -> s.getApproverId() == approverId)
                .findFirst()
                .orElse(null);

        if (step == null) return false;

        requestClient.sendResult(
                req.getRequestId(),
                step.getStep(),
                approverId,
                status
        );

        return true;
    }
}



