package com.microservices.profile_service.grpc;

import com.microservices.profile_service.dto.response.ProfileResponse;
import com.microservices.profile_service.grpc.proto.GetProfilesRequest;
import com.microservices.profile_service.grpc.proto.ProfileGrpcServiceGrpc;
import com.microservices.profile_service.grpc.proto.ProfileListResponse;
import com.microservices.profile_service.service.ProfileService;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.stream.Collectors;

/**
 * gRPC Server Implementation for Profile Service
 * User Service bu endpoint'i gRPC üzerinden çağırır
 */
@GrpcService
public class ProfileGrpcServiceImpl extends ProfileGrpcServiceGrpc.ProfileGrpcServiceImplBase {

        private static final Logger log = LoggerFactory.getLogger(ProfileGrpcServiceImpl.class);

        private final ProfileService profileService;

        public ProfileGrpcServiceImpl(ProfileService profileService) {
                this.profileService = profileService;
        }

        /**
         * gRPC üzerinden account ID'ye göre profilleri getir
         */
        @Override
        public void getProfilesByAccountId(
                        GetProfilesRequest request,
                        StreamObserver<ProfileListResponse> responseObserver) {

                log.info("gRPC: Getting profiles for accountId: {}", request.getAccountId());

                try {
                        // Mevcut service'i kullan
                        List<ProfileResponse> profiles = profileService
                                        .getActiveProfilesByAccountId(request.getAccountId());

                        // DTO list'i gRPC response'a dönüştür
                        List<com.microservices.profile_service.grpc.proto.ProfileResponse> grpcProfiles = profiles
                                        .stream()
                                        .map(profile -> com.microservices.profile_service.grpc.proto.ProfileResponse
                                                        .newBuilder()
                                                        .setId(profile.getId())
                                                        .setProfileName(profile.getProfileName() != null
                                                                        ? profile.getProfileName()
                                                                        : "")
                                                        .setAvatarUrl(profile.getAvatarUrl() != null
                                                                        ? profile.getAvatarUrl()
                                                                        : "")
                                                        .setIsChildProfile(
                                                                        profile.getIsChildProfile() != null
                                                                                        ? profile.getIsChildProfile()
                                                                                        : false)
                                                        .build())
                                        .collect(Collectors.toList());

                        ProfileListResponse grpcResponse = ProfileListResponse.newBuilder()
                                        .addAllProfiles(grpcProfiles)
                                        .build();

                        log.info("gRPC: {} profiles retrieved successfully for accountId: {}", grpcProfiles.size(),
                                        request.getAccountId());

                        responseObserver.onNext(grpcResponse);
                        responseObserver.onCompleted();

                } catch (Exception e) {
                        log.error("gRPC: Failed to get profiles for accountId: {}", request.getAccountId(), e);
                        responseObserver.onError(
                                        io.grpc.Status.NOT_FOUND
                                                        .withDescription("Profiles not found: " + e.getMessage())
                                                        .asRuntimeException());
                }
        }
}
