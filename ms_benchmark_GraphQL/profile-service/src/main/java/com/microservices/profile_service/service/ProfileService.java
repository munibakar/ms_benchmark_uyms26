package com.microservices.profile_service.service;

import com.microservices.profile_service.dto.request.CreateProfileRequest;
import com.microservices.profile_service.dto.request.UpdateProfileRequest;
import com.microservices.profile_service.dto.response.ProfileResponse;
import com.microservices.profile_service.entity.Profile;
import com.microservices.profile_service.exception.BadRequestException;
import com.microservices.profile_service.exception.ResourceNotFoundException;
import com.microservices.profile_service.config.ProfileServiceConfig;
import com.microservices.profile_service.repository.ProfileRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Profile Service - Profile Service
 * Profil yönetimi işlemlerini yönetir
 * 
 * Apollo Federation: Servisler arası iletişim Gateway üzerinden yapılır.
 * User ve Subscription validasyonları client tarafında (GraphQL sorgusu ile) yapılmalıdır.
 */
@Service
public class ProfileService {

    private static final Logger log = LoggerFactory.getLogger(ProfileService.class);

    private final ProfileRepository profileRepository;
    private final PasswordEncoder passwordEncoder;
    private final ProfileServiceConfig profileServiceConfig;

    public ProfileService(
            ProfileRepository profileRepository,
            ProfileServiceConfig profileServiceConfig) {
        this.profileRepository = profileRepository;
        this.profileServiceConfig = profileServiceConfig;
        this.passwordEncoder = new BCryptPasswordEncoder();
    }

    /**
     * Yeni profil oluştur
     * 
     * NOT: Apollo Federation ile User ve Subscription validasyonları 
     * Gateway üzerinden client tarafında yapılmalıdır.
     * Bu metot sadece profil oluşturma işlemini yapar.
     */
    @Transactional
    public ProfileResponse createProfile(CreateProfileRequest request) {
        log.info("Creating profile for accountId: {}, profileName: {}", 
                request.getAccountId(), request.getProfileName());

        // Mevcut profil sayısını kontrol et (lokal kontrol)
        long currentProfileCount = profileRepository.countActiveProfilesByAccountId(request.getAccountId());
        
        // Default max profile limit (Federation ile subscription bilgisi client'tan gelecek)
        int maxProfiles = profileServiceConfig.getDefaultMaxProfiles();
        
        if (currentProfileCount >= maxProfiles) {
            throw new BadRequestException(
                    String.format("Maximum profile limit reached. Maximum allowed: %d", maxProfiles));
        }

        // PIN şifreleme
        String pinHash = null;
        if (request.getIsPinProtected() != null && request.getIsPinProtected() && request.getPin() != null) {
            int pinLength = request.getPin().length();
            if (pinLength < profileServiceConfig.getPinMinLength() || 
                pinLength > profileServiceConfig.getPinMaxLength()) {
                throw new BadRequestException(
                        String.format("PIN must be between %d and %d characters", 
                                profileServiceConfig.getPinMinLength(), 
                                profileServiceConfig.getPinMaxLength()));
            }
            pinHash = passwordEncoder.encode(request.getPin());
        }

        // Varsayılan profil kontrolü
        if (request.getIsDefault() != null && request.getIsDefault()) {
            // Mevcut varsayılan profili kaldır
            profileRepository.findDefaultProfileByAccountId(request.getAccountId())
                    .ifPresent(existingDefault -> {
                        existingDefault.setIsDefault(false);
                        profileRepository.save(existingDefault);
                    });
        } else {
            // Eğer ilk profil ise varsayılan yap
            if (!profileRepository.existsByAccountId(request.getAccountId())) {
                request.setIsDefault(true);
            }
        }

        // Profil oluştur
        Profile profile = Profile.builder()
                .accountId(request.getAccountId())
                .profileName(request.getProfileName())
                .avatarUrl(request.getAvatarUrl())
                .isChildProfile(request.getIsChildProfile() != null ? request.getIsChildProfile() : false)
                .maturityLevel(request.getMaturityLevel() != null ? request.getMaturityLevel() : profileServiceConfig.getDefaultMaturityLevel())
                .language(request.getLanguage() != null ? request.getLanguage() : profileServiceConfig.getDefaultLanguage())
                .isPinProtected(request.getIsPinProtected() != null ? request.getIsPinProtected() : false)
                .pinHash(pinHash)
                .isActive(true)
                .isDefault(request.getIsDefault() != null ? request.getIsDefault() : false)
                .build();

        profile = profileRepository.save(profile);

        log.info("Profile created successfully: profileId={}, accountId={}", 
                profile.getId(), profile.getAccountId());

        return ProfileResponse.fromEntity(profile);
    }

    /**
     * Account ID'ye göre aktif profilleri getir
     */
    @Transactional(readOnly = true)
    public List<ProfileResponse> getActiveProfilesByAccountId(String accountId) {
        log.info("Fetching active profiles for accountId: {}", accountId);

        List<Profile> profiles = profileRepository.findActiveProfilesByAccountId(accountId);

        return profiles.stream()
                .map(ProfileResponse::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * Profile ID'ye göre profil getir
     */
    @Transactional(readOnly = true)
    public ProfileResponse getProfileById(Long profileId) {
        log.info("Fetching profile for profileId: {}", profileId);

        Profile profile = profileRepository.findByIdAndNotDeleted(profileId)
                .orElseThrow(() -> {
                    log.error("Profile not found for profileId: {}", profileId);
                    return new ResourceNotFoundException("Profile not found for profile ID: " + profileId);
                });

        return ProfileResponse.fromEntity(profile);
    }

    /**
     * Account ID ve Profile ID'ye göre profil getir
     */
    @Transactional(readOnly = true)
    public ProfileResponse getProfileByIdAndAccountId(Long profileId, String accountId) {
        log.info("Fetching profile for profileId: {} and accountId: {}", profileId, accountId);

        Profile profile = profileRepository.findByIdAndAccountId(profileId, accountId)
                .orElseThrow(() -> {
                    log.error("Profile not found for profileId: {} and accountId: {}", profileId, accountId);
                    return new ResourceNotFoundException(
                            "Profile not found for profile ID: " + profileId + " and account ID: " + accountId);
                });

        return ProfileResponse.fromEntity(profile);
    }

    /**
     * Profil güncelle
     */
    @Transactional
    public ProfileResponse updateProfile(Long profileId, String accountId, UpdateProfileRequest request) {
        log.info("Updating profile for profileId: {} and accountId: {}", profileId, accountId);

        Profile profile = profileRepository.findByIdAndAccountId(profileId, accountId)
                .orElseThrow(() -> {
                    log.error("Profile not found for profileId: {} and accountId: {}", profileId, accountId);
                    return new ResourceNotFoundException(
                            "Profile not found for profile ID: " + profileId + " and account ID: " + accountId);
                });

        // Profil adı güncelle
        if (request.getProfileName() != null) {
            profile.setProfileName(request.getProfileName());
        }

        // Avatar URL güncelle
        if (request.getAvatarUrl() != null) {
            profile.setAvatarUrl(request.getAvatarUrl());
        }

        // Çocuk profili güncelle
        if (request.getIsChildProfile() != null) {
            profile.setIsChildProfile(request.getIsChildProfile());
        }

        // Olgunluk seviyesi güncelle
        if (request.getMaturityLevel() != null) {
            profile.setMaturityLevel(request.getMaturityLevel());
        }

        // Dil güncelle
        if (request.getLanguage() != null) {
            profile.setLanguage(request.getLanguage());
        }

        // PIN koruması güncelle
        if (request.getIsPinProtected() != null) {
            profile.setIsPinProtected(request.getIsPinProtected());
            if (request.getIsPinProtected() && request.getPin() != null) {
                int pinLength = request.getPin().length();
                if (pinLength < profileServiceConfig.getPinMinLength() || 
                    pinLength > profileServiceConfig.getPinMaxLength()) {
                    throw new BadRequestException(
                            String.format("PIN must be between %d and %d characters", 
                                    profileServiceConfig.getPinMinLength(), 
                                    profileServiceConfig.getPinMaxLength()));
                }
                profile.setPinHash(passwordEncoder.encode(request.getPin()));
            } else if (!request.getIsPinProtected()) {
                profile.setPinHash(null);
            }
        }

        // Varsayılan profil güncelle
        if (request.getIsDefault() != null && request.getIsDefault()) {
            // Mevcut varsayılan profili kaldır
            profileRepository.findDefaultProfileByAccountId(accountId)
                    .ifPresent(existingDefault -> {
                        if (!existingDefault.getId().equals(profileId)) {
                            existingDefault.setIsDefault(false);
                            profileRepository.save(existingDefault);
                        }
                    });
            profile.setIsDefault(true);
        }

        profile = profileRepository.save(profile);

        log.info("Profile updated successfully: profileId={}", profileId);

        return ProfileResponse.fromEntity(profile);
    }

    /**
     * Profil sil (soft delete)
     */
    @Transactional
    public void deleteProfile(Long profileId, String accountId) {
        log.info("Deleting profile for profileId: {} and accountId: {}", profileId, accountId);

        Profile profile = profileRepository.findByIdAndAccountId(profileId, accountId)
                .orElseThrow(() -> {
                    log.error("Profile not found for profileId: {} and accountId: {}", profileId, accountId);
                    return new ResourceNotFoundException(
                            "Profile not found for profile ID: " + profileId + " and account ID: " + accountId);
                });

        // Varsayılan profil kontrolü
        if (profile.getIsDefault()) {
            // Başka aktif profil var mı kontrol et
            List<Profile> otherProfiles = profileRepository.findActiveProfilesByAccountId(accountId)
                    .stream()
                    .filter(p -> !p.getId().equals(profileId))
                    .collect(Collectors.toList());

            if (otherProfiles.isEmpty()) {
                throw new BadRequestException("Cannot delete the last profile. At least one profile must remain.");
            }

            // Başka bir profili varsayılan yap
            otherProfiles.get(0).setIsDefault(true);
            profileRepository.save(otherProfiles.get(0));
        }

        profile.setDeletedAt(LocalDateTime.now());
        profile.setIsActive(false);
        profileRepository.save(profile);

        log.info("Profile deleted successfully: profileId={}", profileId);
    }

    /**
     * Varsayılan profili getir
     */
    @Transactional(readOnly = true)
    public ProfileResponse getDefaultProfile(String accountId) {
        log.info("Fetching default profile for accountId: {}", accountId);

        Profile profile = profileRepository.findDefaultProfileByAccountId(accountId)
                .orElseThrow(() -> {
                    log.error("Default profile not found for accountId: {}", accountId);
                    return new ResourceNotFoundException("Default profile not found for account ID: " + accountId);
                });

        return ProfileResponse.fromEntity(profile);
    }

    /**
     * Profil sayısını getir
     */
    @Transactional(readOnly = true)
    public long getProfileCount(String accountId) {
        return profileRepository.countActiveProfilesByAccountId(accountId);
    }
}
