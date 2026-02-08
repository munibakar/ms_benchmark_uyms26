package com.microservices.video_streaming_service.grpc;

import com.microservices.video_streaming_service.dto.response.ContentResponse;
import com.microservices.video_streaming_service.grpc.proto.ContentGrpcServiceGrpc;
import com.microservices.video_streaming_service.grpc.proto.GetContentRequest;
import io.grpc.StatusRuntimeException;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * gRPC Client for Content Management Service
 * Content Management Service'e gRPC üzerinden bağlanır ve içerik bilgilerini
 * getirir
 */
@Service
public class ContentServiceGrpcClient {

    private static final Logger log = LoggerFactory.getLogger(ContentServiceGrpcClient.class);

    @GrpcClient("content-service")
    private ContentGrpcServiceGrpc.ContentGrpcServiceBlockingStub contentStub;

    /**
     * gRPC üzerinden içerik bilgisini getir
     * 
     * @param contentId İçerik ID
     * @return ContentResponse
     */
    public ContentResponse getContentById(Long contentId) {
        log.info("gRPC Client: Getting content for contentId: {}", contentId);

        try {
            // gRPC request oluştur
            GetContentRequest request = GetContentRequest.newBuilder()
                    .setContentId(contentId)
                    .build();

            // gRPC çağrısı yap
            com.microservices.video_streaming_service.grpc.proto.ContentResponse grpcResponse = contentStub
                    .getContentById(request);

            log.info("gRPC Client: Content retrieved successfully for contentId: {}", contentId);

            // gRPC response'u DTO'ya dönüştür
            return ContentResponse.builder()
                    .id(grpcResponse.getId())
                    .title(grpcResponse.getTitle())
                    .videoFilePath(grpcResponse.getVideoFilePath())
                    .contentType(grpcResponse.getContentType())
                    .isActive(grpcResponse.getIsActive())
                    .build();

        } catch (StatusRuntimeException e) {
            log.error("gRPC Client: Failed to get content for contentId: {}. Status: {}",
                    contentId, e.getStatus(), e);
            throw new RuntimeException("Failed to get content via gRPC: " + e.getStatus().getDescription(), e);
        }
    }
}
