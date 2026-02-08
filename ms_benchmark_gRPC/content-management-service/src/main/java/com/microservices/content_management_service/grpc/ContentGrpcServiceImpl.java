package com.microservices.content_management_service.grpc;

import com.microservices.content_management_service.dto.response.ContentResponse;
import com.microservices.content_management_service.grpc.proto.*;
import com.microservices.content_management_service.service.ContentService;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.stream.Collectors;

/**
 * gRPC Server Implementation for Content Management Service
 * Video Streaming Service bu endpoint'leri gRPC üzerinden çağırır
 */
@GrpcService
public class ContentGrpcServiceImpl extends ContentGrpcServiceGrpc.ContentGrpcServiceImplBase {

    private static final Logger log = LoggerFactory.getLogger(ContentGrpcServiceImpl.class);

    private final ContentService contentService;

    public ContentGrpcServiceImpl(ContentService contentService) {
        this.contentService = contentService;
    }

    /**
     * gRPC üzerinden content ID'ye göre içerik bilgisi getir
     */
    @Override
    public void getContentById(
            GetContentRequest request,
            StreamObserver<com.microservices.content_management_service.grpc.proto.ContentResponse> responseObserver) {

        log.info("gRPC: Getting content for contentId: {}", request.getContentId());

        try {
            // Mevcut service'i kullan
            ContentResponse result = contentService.getContentById(request.getContentId());

            // DTO response'u gRPC response'a dönüştür
            com.microservices.content_management_service.grpc.proto.ContentResponse grpcResponse = com.microservices.content_management_service.grpc.proto.ContentResponse
                    .newBuilder()
                    .setId(result.getId())
                    .setTitle(result.getTitle() != null ? result.getTitle() : "")
                    .setDescription(result.getDescription() != null ? result.getDescription() : "")
                    .setContentType(result.getContentType() != null ? result.getContentType().name() : "")
                    .setReleaseYear(result.getReleaseYear() != null ? result.getReleaseYear() : 0)
                    .setDurationMinutes(result.getDurationMinutes() != null ? result.getDurationMinutes() : 0)
                    .setVideoFilePath(result.getVideoFilePath() != null ? result.getVideoFilePath() : "")
                    .setPosterUrl(result.getPosterUrl() != null ? result.getPosterUrl() : "")
                    .setThumbnailUrl(result.getThumbnailUrl() != null ? result.getThumbnailUrl() : "")
                    .setTrailerUrl(result.getTrailerUrl() != null ? result.getTrailerUrl() : "")
                    .setRating(result.getRating() != null ? result.getRating() : 0.0)
                    .setAgeRating(result.getAgeRating() != null ? result.getAgeRating() : "")
                    .setLanguage(result.getLanguage() != null ? result.getLanguage() : "")
                    .setStatus(result.getStatus() != null ? result.getStatus().name() : "")
                    .setIsFeatured(result.getIsFeatured() != null ? result.getIsFeatured() : false)
                    .setViewCount(result.getViewCount() != null ? result.getViewCount() : 0L)
                    .setTotalSeasons(result.getTotalSeasons() != null ? result.getTotalSeasons() : 0)
                    .setIsActive(result.getIsActive() != null ? result.getIsActive() : false)
                    .setCreatedAt(result.getCreatedAt() != null ? result.getCreatedAt().toString() : "")
                    .setUpdatedAt(result.getUpdatedAt() != null ? result.getUpdatedAt().toString() : "")
                    .build();

            log.info("gRPC: Content retrieved successfully for contentId: {}", request.getContentId());

            responseObserver.onNext(grpcResponse);
            responseObserver.onCompleted();

        } catch (Exception e) {
            log.error("gRPC: Failed to get content for contentId: {}", request.getContentId(), e);
            responseObserver.onError(
                    Status.NOT_FOUND
                            .withDescription("Content not found: " + e.getMessage())
                            .asRuntimeException());
        }
    }

    /**
     * gRPC üzerinden tüm içerikleri getir (dashboard için)
     */
    @Override
    public void getAllContents(
            GetAllContentsRequest request,
            StreamObserver<ContentListResponse> responseObserver) {

        int page = request.getPage();
        int size = request.getSize();

        // Varsayılan değerler: Sayfa 0, Boyut 10 (Eğer client göndermezse)
        if (size <= 0) {
            size = 10;
        }
        if (page < 0) {
            page = 0;
        }

        log.info("gRPC: Getting contents with pagination - Page: {}, Size: {}", page, size);

        try {
            // Sayfalı service'i kullan
            List<ContentResponse> allContents = contentService.getAllActiveContents(page, size);

            // DTO list'i gRPC response'a dönüştür
            List<com.microservices.content_management_service.grpc.proto.ContentResponse> grpcContents = allContents
                    .stream()
                    .map(content -> com.microservices.content_management_service.grpc.proto.ContentResponse.newBuilder()
                            .setId(content.getId())
                            .setTitle(content.getTitle() != null ? content.getTitle() : "")
                            .setDescription(content.getDescription() != null ? content.getDescription() : "")
                            .setContentType(content.getContentType() != null ? content.getContentType().name() : "")
                            .setReleaseYear(content.getReleaseYear() != null ? content.getReleaseYear() : 0)
                            .setDurationMinutes(content.getDurationMinutes() != null ? content.getDurationMinutes() : 0)
                            .setVideoFilePath(content.getVideoFilePath() != null ? content.getVideoFilePath() : "")
                            .setPosterUrl(content.getPosterUrl() != null ? content.getPosterUrl() : "")
                            .setThumbnailUrl(content.getThumbnailUrl() != null ? content.getThumbnailUrl() : "")
                            .setTrailerUrl(content.getTrailerUrl() != null ? content.getTrailerUrl() : "")
                            .setRating(content.getRating() != null ? content.getRating() : 0.0)
                            .setAgeRating(content.getAgeRating() != null ? content.getAgeRating() : "")
                            .setLanguage(content.getLanguage() != null ? content.getLanguage() : "")
                            .setStatus(content.getStatus() != null ? content.getStatus().name() : "")
                            .setIsFeatured(content.getIsFeatured() != null ? content.getIsFeatured() : false)
                            .setViewCount(content.getViewCount() != null ? content.getViewCount() : 0L)
                            .setTotalSeasons(content.getTotalSeasons() != null ? content.getTotalSeasons() : 0)
                            .setIsActive(content.getIsActive() != null ? content.getIsActive() : false)
                            .setCreatedAt(content.getCreatedAt() != null ? content.getCreatedAt().toString() : "")
                            .setUpdatedAt(content.getUpdatedAt() != null ? content.getUpdatedAt().toString() : "")
                            .build())
                    .collect(Collectors.toList());

            com.microservices.content_management_service.grpc.proto.ContentListResponse grpcResponse = com.microservices.content_management_service.grpc.proto.ContentListResponse
                    .newBuilder()
                    .addAllContents(grpcContents)
                    .build();

            log.info("gRPC: {} contents retrieved successfully", grpcContents.size());

            responseObserver.onNext(grpcResponse);
            responseObserver.onCompleted();

        } catch (Exception e) {
            log.error("gRPC: Failed to get all contents", e);
            responseObserver.onError(
                    Status.INTERNAL
                            .withDescription("Failed to get contents: " + e.getMessage())
                            .asRuntimeException());
        }
    }
}
