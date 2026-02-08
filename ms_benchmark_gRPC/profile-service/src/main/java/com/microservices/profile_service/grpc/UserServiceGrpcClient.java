package com.microservices.profile_service.grpc;

import com.microservices.profile_service.dto.response.UserProfileResponse;
import com.microservices.profile_service.grpc.proto.GetUserProfileRequest;
import com.microservices.profile_service.grpc.proto.UserProfileGrpcServiceGrpc;
import io.grpc.StatusRuntimeException;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * gRPC Client for User Service
 * User Service'e gRPC üzerinden bağlanır ve profil işlemlerini gerçekleştirir
 */
@Service
public class UserServiceGrpcClient {

    private static final Logger log = LoggerFactory.getLogger(UserServiceGrpcClient.class);

    @GrpcClient("user-service")
    private UserProfileGrpcServiceGrpc.UserProfileGrpcServiceBlockingStub userProfileStub;

    /**
     * gRPC üzerinden kullanıcı profilini getir
     * 
     * @param userId Kullanıcı ID
     * @return UserProfileResponse
     */
    public UserProfileResponse getUserProfile(String userId) {
        log.info("gRPC Client: Getting user profile for userId: {}", userId);

        try {
            // gRPC request oluştur
            GetUserProfileRequest request = GetUserProfileRequest.newBuilder()
                    .setUserId(userId)
                    .build();

            // gRPC çağrısı yap
            com.microservices.profile_service.grpc.proto.UserProfileResponse grpcResponse = userProfileStub
                    .getUserProfileByUserId(request);

            log.info("gRPC Client: User profile retrieved successfully for userId: {}", grpcResponse.getUserId());

            // gRPC response'u DTO'ya dönüştür
            return UserProfileResponse.builder()
                    .userId(grpcResponse.getUserId())
                    .email(grpcResponse.getEmail())
                    .firstName(grpcResponse.getFirstName())
                    .lastName(grpcResponse.getLastName())
                    .isActive(grpcResponse.getIsActive())
                    .isVerified(grpcResponse.getIsVerified())
                    .build();

        } catch (StatusRuntimeException e) {
            log.error("gRPC Client: Failed to get user profile for userId: {}. Status: {}",
                    userId, e.getStatus(), e);
            throw new RuntimeException("Failed to get user profile via gRPC: " + e.getStatus().getDescription(), e);
        }
    }
}
