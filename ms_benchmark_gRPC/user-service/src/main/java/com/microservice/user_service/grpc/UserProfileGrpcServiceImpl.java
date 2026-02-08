package com.microservice.user_service.grpc;

import com.microservice.user_service.grpc.proto.GetUserProfileRequest;
import com.microservice.user_service.grpc.proto.UserProfileGrpcServiceGrpc;
import com.microservice.user_service.service.UserProfileService;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * gRPC Server Implementation for User Profile Service
 * Authentication Service bu endpoint'leri gRPC üzerinden çağırır
 */
@GrpcService
public class UserProfileGrpcServiceImpl extends UserProfileGrpcServiceGrpc.UserProfileGrpcServiceImplBase {

    private static final Logger log = LoggerFactory.getLogger(UserProfileGrpcServiceImpl.class);

    private final UserProfileService userProfileService;

    public UserProfileGrpcServiceImpl(UserProfileService userProfileService) {
        this.userProfileService = userProfileService;
    }

    /**
     * gRPC üzerinden yeni kullanıcı profili oluştur
     */
    @Override
    public void createUserProfile(
            com.microservice.user_service.grpc.proto.CreateUserProfileRequest request,
            StreamObserver<com.microservice.user_service.grpc.proto.UserProfileResponse> responseObserver) {

        log.info("gRPC: Creating user profile for userId: {}, email: {}",
                request.getUserId(), request.getEmail());

        try {
            // gRPC request'i mevcut DTO'ya dönüştür
            com.microservice.user_service.dto.request.CreateUserProfileRequest dtoRequest = com.microservice.user_service.dto.request.CreateUserProfileRequest
                    .builder()
                    .userId(request.getUserId())
                    .email(request.getEmail())
                    .firstName(request.getFirstName())
                    .lastName(request.getLastName())
                    .build();

            // Mevcut service'i kullan
            com.microservice.user_service.dto.response.UserProfileResponse result = userProfileService
                    .createUserProfile(dtoRequest);

            // DTO response'u gRPC response'a dönüştür
            com.microservice.user_service.grpc.proto.UserProfileResponse grpcResponse = com.microservice.user_service.grpc.proto.UserProfileResponse
                    .newBuilder()
                    .setUserId(result.getUserId())
                    .setEmail(result.getEmail())
                    .setFirstName(result.getFirstName() != null ? result.getFirstName() : "")
                    .setLastName(result.getLastName() != null ? result.getLastName() : "")
                    .setIsActive(result.getIsActive() != null ? result.getIsActive() : false)
                    .setIsVerified(result.getIsVerified() != null ? result.getIsVerified() : false)
                    .setCreatedAt(result.getCreatedAt() != null ? result.getCreatedAt().toString() : "")
                    .setUpdatedAt(result.getUpdatedAt() != null ? result.getUpdatedAt().toString() : "")
                    .build();

            log.info("gRPC: User profile created successfully for userId: {}", result.getUserId());

            responseObserver.onNext(grpcResponse);
            responseObserver.onCompleted();

        } catch (Exception e) {
            log.error("gRPC: Failed to create user profile for userId: {}", request.getUserId(), e);
            responseObserver.onError(
                    io.grpc.Status.INTERNAL
                            .withDescription("Failed to create user profile: " + e.getMessage())
                            .asRuntimeException());
        }
    }

    /**
     * gRPC üzerinden kullanıcı ID'sine göre profil getir
     */
    @Override
    public void getUserProfileByUserId(
            GetUserProfileRequest request,
            StreamObserver<com.microservice.user_service.grpc.proto.UserProfileResponse> responseObserver) {

        log.info("gRPC: Getting user profile for userId: {}", request.getUserId());

        try {
            com.microservice.user_service.dto.response.UserProfileResponse result = userProfileService
                    .getUserProfileByUserId(request.getUserId());

            com.microservice.user_service.grpc.proto.UserProfileResponse grpcResponse = com.microservice.user_service.grpc.proto.UserProfileResponse
                    .newBuilder()
                    .setUserId(result.getUserId())
                    .setEmail(result.getEmail())
                    .setFirstName(result.getFirstName() != null ? result.getFirstName() : "")
                    .setLastName(result.getLastName() != null ? result.getLastName() : "")
                    .setIsActive(result.getIsActive() != null ? result.getIsActive() : false)
                    .setIsVerified(result.getIsVerified() != null ? result.getIsVerified() : false)
                    .setCreatedAt(result.getCreatedAt() != null ? result.getCreatedAt().toString() : "")
                    .setUpdatedAt(result.getUpdatedAt() != null ? result.getUpdatedAt().toString() : "")
                    .build();

            log.info("gRPC: User profile retrieved successfully for userId: {}", result.getUserId());

            responseObserver.onNext(grpcResponse);
            responseObserver.onCompleted();

        } catch (Exception e) {
            log.error("gRPC: Failed to get user profile for userId: {}", request.getUserId(), e);
            responseObserver.onError(
                    io.grpc.Status.NOT_FOUND
                            .withDescription("User profile not found: " + e.getMessage())
                            .asRuntimeException());
        }
    }
}
