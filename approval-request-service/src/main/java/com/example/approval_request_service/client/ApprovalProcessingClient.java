package com.example.approval_request_service.client;

import com.example.approval_request_service.grpc.ApprovalGrpc;
import com.example.approval_request_service.grpc.ApprovalRequest;
import com.example.approval_request_service.domain.Step;

import com.example.approval_request_service.grpc.ApprovalResponse;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import jakarta.annotation.PreDestroy;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ApprovalProcessingClient {

    private final ManagedChannel channel;
    private final ApprovalGrpc.ApprovalBlockingStub blockingStub;

    public ApprovalProcessingClient(
            @Value("${approval-processing.host}") String host,
            @Value("${approval-processing.port}") int port
    ) {
        this.channel = ManagedChannelBuilder
                .forAddress(host, port)
                .usePlaintext() // TLS 안 쓰는 로컬 개발용
                .build();
        this.blockingStub = ApprovalGrpc.newBlockingStub(channel);
    }

    @PreDestroy
    public void shutdown() {
        log.info("Shutting down gRPC channel to Approval Processing Service");
        channel.shutdown();
    }

    public void sendApprovalRequest(
            Long requestId,
            Long requesterId,
            String title,
            String content,
            List<Step> domainSteps
    ) {
        // Domain Step -> gRPC Step 변환
        List<com.example.approval_request_service.grpc.Step> grpcSteps = domainSteps.stream()
                .map(s -> com.example.approval_request_service.grpc.Step.newBuilder()
                        .setStep(s.getStep())
                        .setApproverId(Math.toIntExact(s.getApproverId()))
                        .setStatus(s.getStatus())
                        .build()
                )
                .toList();

        ApprovalRequest grpcRequest = ApprovalRequest.newBuilder()
                .setRequestId(requestId.intValue())
                .setRequesterId(requesterId.intValue())
                .setTitle(title)
                .setContent(content)
                .addAllSteps(grpcSteps)
                .build();

        log.info("Sending ApprovalRequest via gRPC: requestId={}", requestId);

        ApprovalResponse response = blockingStub.requestApproval(grpcRequest);

        log.info("Received gRPC response from Processing Service: status={}", response.getStatus());
    }
}
