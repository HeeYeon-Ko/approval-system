package com.example.approval_processing_service.config;

import java.io.IOException;
import org.springframework.stereotype.Component;
import com.example.approval_processing_service.grpc.ApprovalGrpcServer;
import io.grpc.Server;
import io.grpc.netty.shaded.io.grpc.netty.NettyServerBuilder;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;

@Component
public class GrpcServerConfig {

    private Server server;
    private final ApprovalGrpcServer approvalGrpcServer;

    public GrpcServerConfig(ApprovalGrpcServer approvalGrpcServer) {
        this.approvalGrpcServer = approvalGrpcServer;
    }

    @PostConstruct
    public void start() throws IOException {
        server = NettyServerBuilder.forPort(9090)
                .addService(approvalGrpcServer)
                .build()
                .start();

        System.out.println("gRPC Server started on port 9090");
    }

    @PreDestroy
    public void stop() {
        if (server != null) server.shutdown();
    }
}
