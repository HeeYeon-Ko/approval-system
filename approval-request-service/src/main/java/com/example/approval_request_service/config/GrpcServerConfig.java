package com.example.approval_request_service.config;

import com.example.approval_request_service.grpc.ApprovalResultGrpcServer;
import io.grpc.Server;
import io.grpc.netty.shaded.io.grpc.netty.NettyServerBuilder;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class GrpcServerConfig {

    private Server server;
    private final ApprovalResultGrpcServer approvalResultGrpcServer;

    public GrpcServerConfig(ApprovalResultGrpcServer approvalResultGrpcServer) {
        this.approvalResultGrpcServer = approvalResultGrpcServer;
    }

    @PostConstruct
    public void start() throws IOException {
        server = NettyServerBuilder.forPort(9091)
                .addService(approvalResultGrpcServer)
                .build()
                .start();

        System.out.println("Approval Request gRPC Server started on port 9091");
    }

    @PreDestroy
    public void stop() {
        if (server != null) {
            server.shutdown();
        }
    }
}
