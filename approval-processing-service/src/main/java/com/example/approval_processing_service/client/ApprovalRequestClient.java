package com.example.approval_processing_service.client;

import com.example.approval.grpc.ApprovalGrpc;
import com.example.approval.grpc.ApprovalResultRequest;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ApprovalRequestClient {

    private final ManagedChannel channel;
    private final ApprovalGrpc.ApprovalBlockingStub stub;

    public ApprovalRequestClient(
            @Value("${approval-request.host}") String host,
            @Value("${approval-request.port}") int port
    ) {
        this.channel = ManagedChannelBuilder.forAddress(host, port)
                .usePlaintext()
                .build();

        this.stub = ApprovalGrpc.newBlockingStub(channel);
    }

    @PreDestroy
    public void shutdown() {
        channel.shutdown();
    }

    public void sendResult(int requestId, int step, int approverId, String status) {
        ApprovalResultRequest req = ApprovalResultRequest.newBuilder()
                .setRequestId(requestId)
                .setStep(step)
                .setApproverId(approverId)
                .setStatus(status)
                .build();

        stub.returnApprovalResult(req);
    }
}
