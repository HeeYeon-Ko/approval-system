package com.example.approval_processing_service.grpc;

import com.example.approval_processing_service.service.ProcessingQueueService;
import org.springframework.stereotype.Component;
import io.grpc.stub.StreamObserver;

@Component
public class ApprovalGrpcServer extends ApprovalGrpc.ApprovalImplBase{

    private final ProcessingQueueService queue;

    public ApprovalGrpcServer(ProcessingQueueService queue) {
        this.queue = queue;
    }

    @Override
    public void requestApproval(
            ApprovalRequest request,
            StreamObserver<ApprovalResponse> responseObserver
    ) {
        queue.enqueue(request);

        ApprovalResponse response = ApprovalResponse.newBuilder()
                .setStatus("received")
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}
