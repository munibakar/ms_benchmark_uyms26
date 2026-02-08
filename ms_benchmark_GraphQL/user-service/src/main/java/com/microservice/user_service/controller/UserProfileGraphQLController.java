package com.microservice.user_service.controller;

import com.microservice.user_service.dto.request.CreateUserProfileRequest;
import com.microservice.user_service.dto.request.UpdateUserProfileRequest;
import com.microservice.user_service.dto.response.UserProfileResponse;
import com.microservice.user_service.service.UserProfileService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.time.LocalDate;
import java.util.List;

/**
 * User Profile GraphQL Controller
 * GraphQL mutation ve query'leri handle eder
 * Apollo Federation subgraph olarak çalışır
 */
@Controller
public class UserProfileGraphQLController {

    private static final Logger log = LoggerFactory.getLogger(UserProfileGraphQLController.class);

    private final UserProfileService userProfileService;

    public UserProfileGraphQLController(UserProfileService userProfileService) {
        this.userProfileService = userProfileService;
    }

    // ============== QUERIES ==============

    /**
     * GraphQL Query: user(id: ID!)
     * Federation entity query - Gateway'den gelen user sorgularını handle eder
     */
    @QueryMapping
    public UserProfileResponse user(@Argument String id) {
        log.info("GraphQL Query: user for id: {}", id);
        try {
            Long userId = Long.parseLong(id);
            return userProfileService.getUserProfileById(userId);
        } catch (NumberFormatException e) {
            // ID string ise userId olarak ara
            return userProfileService.getUserProfileByUserId(id);
        }
    }

    /**
     * GraphQL Query: getUserProfile
     * UserId'ye göre kullanıcı profili getir
     */
    @QueryMapping
    public UserProfileResponse getUserProfile(@Argument String userId) {
        log.info("GraphQL Query: getUserProfile for userId: {}", userId);
        return userProfileService.getUserProfileByUserId(userId);
    }

    /**
     * GraphQL Query: getUserProfileByEmail
     * Email'e göre kullanıcı profili getir
     */
    @QueryMapping
    public UserProfileResponse getUserProfileByEmail(@Argument String email) {
        log.info("GraphQL Query: getUserProfileByEmail for email: {}", email);
        return userProfileService.getUserProfileByEmail(email);
    }

    /**
     * GraphQL Query: getAllUserProfiles
     * Tüm kullanıcı profillerini listele
     */
    @QueryMapping
    public List<UserProfileResponse> getAllUserProfiles() {
        log.info("GraphQL Query: getAllUserProfiles");
        return userProfileService.getAllUserProfiles();
    }

    // ============== MUTATIONS ==============

    /**
     * GraphQL Mutation: createUserProfile
     * Auth Service'ten gelen kullanıcı profili oluşturma isteği
     */
    @MutationMapping
    public UserProfileResponse createUserProfile(@Argument("input") CreateUserProfileInput input) {
        log.info("GraphQL Mutation: createUserProfile for userId: {}", input.userId());

        CreateUserProfileRequest request = CreateUserProfileRequest.builder()
                .userId(input.userId())
                .email(input.email())
                .firstName(input.firstName())
                .lastName(input.lastName())
                .build();

        return userProfileService.createUserProfile(request);
    }

    /**
     * GraphQL Mutation: updateUserProfile
     * Kullanıcı profilini güncelle
     */
    @MutationMapping
    public UserProfileResponse updateUserProfile(@Argument String userId,
            @Argument("input") UpdateUserProfileInput input) {
        log.info("GraphQL Mutation: updateUserProfile for userId: {}", userId);

        UpdateUserProfileRequest request = UpdateUserProfileRequest.builder()
                .firstName(input.firstName())
                .lastName(input.lastName())
                .phoneNumber(input.phoneNumber())
                .dateOfBirth(input.dateOfBirth() != null ? LocalDate.parse(input.dateOfBirth()) : null)
                .profilePictureUrl(input.profilePictureUrl())
                .bio(input.bio())
                .country(input.country())
                .city(input.city())
                .address(input.address())
                .postalCode(input.postalCode())
                .build();

        return userProfileService.updateUserProfile(userId, request);
    }

    /**
     * GraphQL Mutation: deleteUserProfile
     * Kullanıcı profilini sil (soft delete)
     */
    @MutationMapping
    public Boolean deleteUserProfile(@Argument String userId) {
        log.info("GraphQL Mutation: deleteUserProfile for userId: {}", userId);
        userProfileService.deleteUserProfile(userId);
        return true;
    }

    // ============== INPUT RECORDS ==============

    /**
     * GraphQL Input type for createUserProfile mutation
     */
    public record CreateUserProfileInput(
            String userId,
            String email,
            String firstName,
            String lastName) {
    }

    /**
     * GraphQL Input type for updateUserProfile mutation
     */
    public record UpdateUserProfileInput(
            String firstName,
            String lastName,
            String phoneNumber,
            String dateOfBirth,
            String profilePictureUrl,
            String bio,
            String country,
            String city,
            String address,
            String postalCode) {
    }
}
