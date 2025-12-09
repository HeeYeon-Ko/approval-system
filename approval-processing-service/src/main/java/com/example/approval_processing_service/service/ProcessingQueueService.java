package com.example.approval_processing_service.service;

import org.springframework.stereotype.Service;
import com.example.approval_processing_service.grpc.ApprovalRequest;
import com.example.approval_processing_service.grpc.Step;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class ProcessingQueueService {

    private final Map<Integer, List<ApprovalRequest>> queue = new ConcurrentHashMap<>();

    public void enqueue(ApprovalRequest request) {
        Step pending = request.getStepsList().stream()
                .filter(s -> "pending".equals(s.getStatus()))
                .findFirst()
                .orElse(null);

        if (pending == null) return;

        int approverId = pending.getApproverId();

        queue.computeIfAbsent(approverId, k -> new ArrayList<>()).add(request);
    }

    public List<ApprovalRequest> getQueue(int approverId) {
        return queue.getOrDefault(approverId, List.of());
    }

    public ApprovalRequest pop(int approverId, int requestId) {
        List<ApprovalRequest> list = queue.get(approverId);
        if (list == null) return null;

        ApprovalRequest target = list.stream()
                .filter(req -> req.getRequestId() == requestId)
                .findFirst()
                .orElse(null);

        if (target != null) list.remove(target);
        return target;
    }
}
