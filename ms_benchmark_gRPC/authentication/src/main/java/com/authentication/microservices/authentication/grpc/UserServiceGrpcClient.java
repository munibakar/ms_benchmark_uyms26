package com.authentication.microservices.authentication.grpc;

import com.authentication.microservices.authentication.dto.response.UserProfileResponse;
import com.authentication.microservices.authentication.grpc.proto.CreateUserProfileRequest;
import com.authentication.microservices.authentication.grpc.proto.UserProfileGrpcServiceGrpc;
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
     * gRPC üzerinden yeni kullanıcı profili oluştur
     * 
     * @param userId    Kullanıcı ID
     * @param email     Kullanıcı email
     * @param firstName Ad
     * @param lastName  Soyad
     * @return UserProfileResponse
     */
    public UserProfileResponse createUserProfile(String userId, String email, String firstName, String lastName) {
        log.info("gRPC Client: Creating user profile for userId: {}, email: {}", userId, email);

        try {
            // gRPC request oluştur
            CreateUserProfileRequest request = CreateUserProfileRequest.newBuilder()
                    .setUserId(userId)
                    .setEmail(email)
                    .setFirstName(firstName != null ? firstName : "")
                    .setLastName(lastName != null ? lastName : "")
                    .build();

            // gRPC çağrısı yap
            com.authentication.microservices.authentication.grpc.proto.UserProfileResponse grpcResponse = userProfileStub
                    .createUserProfile(request);

            log.info("gRPC Client: User profile created successfully for userId: {}", grpcResponse.getUserId());

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
            log.error("gRPC Client: Failed to create user profile for userId: {}. Status: {}",
                    userId, e.getStatus(), e);
            throw new RuntimeException("Failed to create user profile via gRPC: " + e.getStatus().getDescription(), e);
        }
    }
}
