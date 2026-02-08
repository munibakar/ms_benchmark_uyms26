package com.microservice.user_service.grpc.client;

import com.microservice.user_service.dto.response.UserDashboardResponse.ContentInfo;
import com.microservice.user_service.grpc.proto.ContentGrpcServiceGrpc;
import com.microservice.user_service.grpc.proto.ContentListResponse;
import com.microservice.user_service.grpc.proto.GetAllContentsRequest;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * gRPC Client for Content Service
 * 
 * REAL gRPC implementation using @GrpcClient stub
 */
@Service
public class ContentServiceGrpcClient {

    private static final Logger log = LoggerFactory.getLogger(ContentServiceGrpcClient.class);

    @GrpcClient("content-service")
    private ContentGrpcServiceGrpc.ContentGrpcServiceBlockingStub contentStub;

    /**
     * gRPC üzerinden önerilen içerikleri getir
     */
    public List<ContentInfo> getAllContents() {
        long startTime = System.currentTimeMillis();
        log.info("gRPC Client: Getting recommended contents (REAL gRPC CALL)");

        try {
            // REAL gRPC stub call
            GetAllContentsRequest request = GetAllContentsRequest.newBuilder()
                    .setPage(0)
                    .setSize(100)
                    .build();

            ContentListResponse response = contentStub.getAllContents(request);

            // gRPC response'u DTO'ya dönüştür
            List<ContentInfo> contents = response.getContentsList().stream()
                    .map(grpcContent -> ContentInfo.builder()
                            .id(grpcContent.getId())
                            .title(grpcContent.getTitle())
                            .contentType(grpcContent.getContentType())
                            .build())
                    .collect(Collectors.toList());

            long duration = System.currentTimeMillis() - startTime;
            log.info("gRPC Client: {} contents retrieved successfully - {}ms (REAL gRPC)", contents.size(), duration);

            return contents;

        } catch (Exception e) {
            long duration = System.currentTimeMillis() - startTime;
            log.error("gRPC Client: Failed after {}ms - {}", duration, e.getMessage());
            throw new RuntimeException("Failed to get contents via gRPC", e);
        }
    }
}
