package com.example.approval_request_service.grpc;

import com.example.approval.grpc.ApprovalGrpc;
import com.example.approval.grpc.ApprovalResultRequest;
import com.example.approval.grpc.ApprovalResultResponse;
import com.example.approval_request_service.service.ApprovalService;
import io.grpc.stub.StreamObserver;
import org.springframework.stereotype.Component;

@Component
public class ApprovalResultGrpcServer extends ApprovalGrpc.ApprovalImplBase{
    private final ApprovalService approvalService;

    public ApprovalResultGrpcServer(ApprovalService approvalService) {
        this.approvalService = approvalService;
    }

    @Override
    public void returnApprovalResult(
            ApprovalResultRequest request,
            StreamObserver<ApprovalResultResponse> responseObserver
    ) {
        approvalService.handleApprovalResult(
                request.getRequestId(),
                request.getStep(),
                request.getApproverId(),
                request.getStatus()
        );

        ApprovalResultResponse response = ApprovalResultResponse.newBuilder()
                .setStatus("ok")
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}
