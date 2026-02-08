package com.microservices.profile_service.controller;

import com.microservices.profile_service.dto.request.CreateProfileRequest;
import com.microservices.profile_service.dto.request.UpdateProfileRequest;
import com.microservices.profile_service.dto.response.ProfileResponse;
import com.microservices.profile_service.service.ProfileService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.util.List;

/**
 * Profile Service - GraphQL Controller
 * Tüm GraphQL query ve mutation'larını handle eder
 */
@Controller
public class ProfileGraphQLController {

    private static final Logger log = LoggerFactory.getLogger(ProfileGraphQLController.class);

    private final ProfileService profileService;

    public ProfileGraphQLController(ProfileService profileService) {
        this.profileService = profileService;
    }

    // ============== Queries ==============

    /**
     * GraphQL Query: profile(id: ID!)
     * Federation entity query - Profile ID'ye göre profil getir
     */
    @QueryMapping
    public ProfileResponse profile(@Argument String id) {
        log.info("GraphQL Query: profile for id: {}", id);
        try {
            Long profileId = Long.parseLong(id);
            return profileService.getProfileById(profileId);
        } catch (NumberFormatException e) {
            log.error("Invalid profile id format: {}", id);
            return null;
        }
    }

    /**
     * GraphQL Query: getActiveProfilesByAccountId
     * Account ID'ye göre aktif profilleri getir
     */
    @QueryMapping
    public List<ProfileResponse> getActiveProfilesByAccountId(@Argument String accountId) {
        log.info("GraphQL Query: getActiveProfilesByAccountId for accountId: {}", accountId);
        return profileService.getActiveProfilesByAccountId(accountId);
    }

    /**
     * GraphQL Query: getProfileById
     * Profile ID'ye göre profil getir
     */
    @QueryMapping
    public ProfileResponse getProfileById(@Argument Long profileId) {
        log.info("GraphQL Query: getProfileById for profileId: {}", profileId);
        return profileService.getProfileById(profileId);
    }

    /**
     * GraphQL Query: getProfileByIdAndAccountId
     * Account ID ve Profile ID'ye göre profil getir
     */
    @QueryMapping
    public ProfileResponse getProfileByIdAndAccountId(@Argument Long profileId, @Argument String accountId) {
        log.info("GraphQL Query: getProfileByIdAndAccountId for profileId: {} and accountId: {}",
                profileId, accountId);
        return profileService.getProfileByIdAndAccountId(profileId, accountId);
    }

    /**
     * GraphQL Query: getDefaultProfile
     * Varsayılan profili getir
     */
    @QueryMapping
    public ProfileResponse getDefaultProfile(@Argument String accountId) {
        log.info("GraphQL Query: getDefaultProfile for accountId: {}", accountId);
        return profileService.getDefaultProfile(accountId);
    }

    /**
     * GraphQL Query: getProfileCount
     * Profil sayısını getir
     */
    @QueryMapping
    public Long getProfileCount(@Argument String accountId) {
        log.info("GraphQL Query: getProfileCount for accountId: {}", accountId);
        return profileService.getProfileCount(accountId);
    }

    // ============== Mutations ==============

    /**
     * GraphQL Mutation: createProfile
     * Yeni profil oluştur
     */
    @MutationMapping
    public ProfileResponse createProfile(@Argument("input") CreateProfileInput input) {
        log.info("GraphQL Mutation: createProfile for accountId: {}", input.accountId());

        CreateProfileRequest request = CreateProfileRequest.builder()
                .accountId(input.accountId())
                .profileName(input.profileName())
                .avatarUrl(input.avatarUrl())
                .isChildProfile(input.isChildProfile() != null ? input.isChildProfile() : false)
                .maturityLevel(input.maturityLevel() != null ? input.maturityLevel() : "ALL")
                .language(input.language() != null ? input.language() : "tr")
                .isPinProtected(input.isPinProtected() != null ? input.isPinProtected() : false)
                .pin(input.pin())
                .isDefault(input.isDefault() != null ? input.isDefault() : false)
                .build();

        return profileService.createProfile(request);
    }

    /**
     * GraphQL Mutation: updateProfile
     * Profil güncelle
     */
    @MutationMapping
    public ProfileResponse updateProfile(
            @Argument Long profileId,
            @Argument String accountId,
            @Argument("input") UpdateProfileInput input) {
        log.info("GraphQL Mutation: updateProfile for profileId: {} and accountId: {}",
                profileId, accountId);

        UpdateProfileRequest request = UpdateProfileRequest.builder()
                .profileName(input.profileName())
                .avatarUrl(input.avatarUrl())
                .isChildProfile(input.isChildProfile())
                .maturityLevel(input.maturityLevel())
                .language(input.language())
                .isPinProtected(input.isPinProtected())
                .pin(input.pin())
                .isDefault(input.isDefault())
                .build();

        return profileService.updateProfile(profileId, accountId, request);
    }

    /**
     * GraphQL Mutation: deleteProfile
     * Profil sil (soft delete)
     */
    @MutationMapping
    public Boolean deleteProfile(@Argument Long profileId, @Argument String accountId) {
        log.info("GraphQL Mutation: deleteProfile for profileId: {} and accountId: {}",
                profileId, accountId);

        profileService.deleteProfile(profileId, accountId);
        return true;
    }

    // ============== Federation Resolvers ==============

    /**
     * Federation Resolver: User.profiles
     * Gateway, User entity'sini resolve ederken bu methodu çağırır
     */
    @org.springframework.graphql.data.method.annotation.SchemaMapping(typeName = "User")
    public List<ProfileResponse> profiles(User user) {
        log.info("Federation Resolver: Resolving profiles for userId: {}", user.userId());
        return profileService.getActiveProfilesByAccountId(user.userId());
    }

    // ============== Input Records ==============

    /**
     * Federation User Entity Stub
     */
    public record User(String userId) {
    }

    /**
     * GraphQL Input type for createProfile mutation
     */
    public record CreateProfileInput(
            String accountId,
            String profileName,
            String avatarUrl,
            Boolean isChildProfile,
            String maturityLevel,
            String language,
            Boolean isPinProtected,
            String pin,
            Boolean isDefault) {
    }

    /**
     * GraphQL Input type for updateProfile mutation
     */
    public record UpdateProfileInput(
            String profileName,
            String avatarUrl,
            Boolean isChildProfile,
            String maturityLevel,
            String language,
            Boolean isPinProtected,
            String pin,
            Boolean isDefault) {
    }
}
