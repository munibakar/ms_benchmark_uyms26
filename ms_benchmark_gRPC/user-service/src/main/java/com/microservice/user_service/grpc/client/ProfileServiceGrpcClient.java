package com.microservice.user_service.grpc.client;

import com.microservice.user_service.dto.response.UserDashboardResponse.ProfileInfo;
import com.microservice.user_service.grpc.proto.GetProfilesRequest;
import com.microservice.user_service.grpc.proto.ProfileGrpcServiceGrpc;
import com.microservice.user_service.grpc.proto.ProfileListResponse;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * gRPC Client for Profile Service
 * 
 * REAL gRPC implementation using @GrpcClient stub
 */
@Service
public class ProfileServiceGrpcClient {

    private static final Logger log = LoggerFactory.getLogger(ProfileServiceGrpcClient.class);

    @GrpcClient("profile-service")
    private ProfileGrpcServiceGrpc.ProfileGrpcServiceBlockingStub profileStub;

    /**
     * gRPC üzerinden kullanıcının profillerini getir
     */
    public List<ProfileInfo> getProfilesByAccountId(String accountId) {
        long startTime = System.currentTimeMillis();
        log.info("gRPC Client: Getting profiles for accountId: {} (REAL gRPC CALL)", accountId);

        try {
            // REAL gRPC stub call
            GetProfilesRequest request = GetProfilesRequest.newBuilder()
                    .setAccountId(accountId)
                    .build();

            ProfileListResponse response = profileStub.getProfilesByAccountId(request);

            // gRPC response'u DTO'ya dönüştür
            List<ProfileInfo> profiles = response.getProfilesList().stream()
                    .map(grpcProfile -> ProfileInfo.builder()
                            .id(grpcProfile.getId())
                            .profileName(grpcProfile.getProfileName())
                            .avatarUrl(grpcProfile.getAvatarUrl())
                            .isChildProfile(grpcProfile.getIsChildProfile())
                            .build())
                    .collect(Collectors.toList());

            long duration = System.currentTimeMillis() - startTime;
            log.info("gRPC Client: {} profiles retrieved successfully - {}ms (REAL gRPC)", profiles.size(), duration);

            return profiles;

        } catch (Exception e) {
            long duration = System.currentTimeMillis() - startTime;
            log.error("gRPC Client: Failed after {}ms - {}", duration, e.getMessage());
            throw new RuntimeException("Failed to get profiles via gRPC", e);
        }
    }
}
