package com.microservice.user_service.service;

import com.microservice.user_service.dto.request.CreateUserProfileRequest;
import com.microservice.user_service.dto.request.UpdateUserProfileRequest;
import com.microservice.user_service.dto.response.UserProfileResponse;
import com.microservice.user_service.entity.UserProfile;
import com.microservice.user_service.exception.BadRequestException;
import com.microservice.user_service.exception.ResourceNotFoundException;
import com.microservice.user_service.repository.UserProfileRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * User Profile Service - User Service
 * Kullanıcı profil bilgilerini yönetir
 */
@Service
public class UserProfileService {

    private static final Logger log = LoggerFactory.getLogger(UserProfileService.class);

    private final UserProfileRepository userProfileRepository;

    public UserProfileService(UserProfileRepository userProfileRepository) {
        this.userProfileRepository = userProfileRepository;
    }

    /**
     * Yeni kullanıcı profili oluştur (Auth Service'ten çağrılır)
     */
    @Transactional
    public UserProfileResponse createUserProfile(CreateUserProfileRequest request) {
        log.info("Creating user profile for userId: {}, email: {}", request.getUserId(), request.getEmail());

        // Kullanıcı ID'si veya email ile daha önce profil oluşturulmuş mu kontrol et
        if (userProfileRepository.existsByUserId(request.getUserId())) {
            log.error("User profile already exists for userId: {}", request.getUserId());
            throw new BadRequestException("User profile already exists for this user ID");
        }

        if (userProfileRepository.existsByEmail(request.getEmail())) {
            log.error("User profile already exists for email: {}", request.getEmail());
            throw new BadRequestException("User profile already exists for this email");
        }

        // Yeni kullanıcı profili oluştur
        UserProfile userProfile = UserProfile.builder()
                .userId(request.getUserId())
                .email(request.getEmail())
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .isActive(true)
                .isVerified(false)
                .build();

        userProfile = userProfileRepository.save(userProfile);

        log.info("User profile created successfully for userId: {}", userProfile.getUserId());

        return UserProfileResponse.fromEntity(userProfile);
    }

    /**
     * Kullanıcı ID'sine göre profil getir
     */
    @Transactional(readOnly = true)
    public UserProfileResponse getUserProfileByUserId(String userId) {
        log.info("Fetching user profile for userId: {}", userId);

        UserProfile userProfile = userProfileRepository.findByUserId(userId)
                .orElseThrow(() -> {
                    log.error("User profile not found for userId: {}", userId);
                    return new ResourceNotFoundException("User profile not found for user ID: " + userId);
                });

        return UserProfileResponse.fromEntity(userProfile);
    }

    /**
     * Email'e göre profil getir
     */
    @Transactional(readOnly = true)
    public UserProfileResponse getUserProfileByEmail(String email) {
        log.info("Fetching user profile for email: {}", email);

        UserProfile userProfile = userProfileRepository.findByEmail(email)
                .orElseThrow(() -> {
                    log.error("User profile not found for email: {}", email);
                    return new ResourceNotFoundException("User profile not found for email: " + email);
                });

        return UserProfileResponse.fromEntity(userProfile);
    }

    /**
     * Kullanıcı profilini güncelle
     */
    @Transactional
    public UserProfileResponse updateUserProfile(String userId, UpdateUserProfileRequest request) {
        log.info("Updating user profile for userId: {}", userId);

        UserProfile userProfile = userProfileRepository.findByUserId(userId)
                .orElseThrow(() -> {
                    log.error("User profile not found for userId: {}", userId);
                    return new ResourceNotFoundException("User profile not found for user ID: " + userId);
                });

        // Email değişikliği varsa ve başka kullanıcı tarafından kullanılıyorsa hata ver
        if (request.getEmail() != null && !request.getEmail().equals(userProfile.getEmail())) {
            if (userProfileRepository.existsByEmail(request.getEmail())) {
                log.error("Email already in use: {}", request.getEmail());
                throw new BadRequestException("Email is already in use");
            }
            userProfile.setEmail(request.getEmail());
        }

        // Diğer alanları güncelle
        if (request.getFirstName() != null) {
            userProfile.setFirstName(request.getFirstName());
        }
        if (request.getLastName() != null) {
            userProfile.setLastName(request.getLastName());
        }
        if (request.getPhoneNumber() != null) {
            userProfile.setPhoneNumber(request.getPhoneNumber());
        }
        if (request.getDateOfBirth() != null) {
            userProfile.setDateOfBirth(request.getDateOfBirth());
        }
        if (request.getProfilePictureUrl() != null) {
            userProfile.setProfilePictureUrl(request.getProfilePictureUrl());
        }
        if (request.getBio() != null) {
            userProfile.setBio(request.getBio());
        }
        if (request.getCountry() != null) {
            userProfile.setCountry(request.getCountry());
        }
        if (request.getCity() != null) {
            userProfile.setCity(request.getCity());
        }
        if (request.getAddress() != null) {
            userProfile.setAddress(request.getAddress());
        }
        if (request.getPostalCode() != null) {
            userProfile.setPostalCode(request.getPostalCode());
        }

        userProfile = userProfileRepository.save(userProfile);

        log.info("User profile updated successfully for userId: {}", userId);

        return UserProfileResponse.fromEntity(userProfile);
    }

    /**
     * Kullanıcı profilini sil (soft delete)
     */
    @Transactional
    public void deleteUserProfile(String userId) {
        log.info("Deleting user profile for userId: {}", userId);

        UserProfile userProfile = userProfileRepository.findByUserId(userId)
                .orElseThrow(() -> {
                    log.error("User profile not found for userId: {}", userId);
                    return new ResourceNotFoundException("User profile not found for user ID: " + userId);
                });

        userProfile.setDeletedAt(LocalDateTime.now());
        userProfile.setIsActive(false);
        userProfileRepository.save(userProfile);

        log.info("User profile deleted successfully for userId: {}", userId);
    }

    /**
     * Tüm aktif kullanıcı profillerini listele
     */
    @Transactional(readOnly = true)
    public List<UserProfileResponse> getAllUserProfiles() {
        log.info("Fetching all user profiles");

        List<UserProfile> userProfiles = userProfileRepository.findAll();

        return userProfiles.stream()
                .filter(profile -> profile.getDeletedAt() == null)
                .map(UserProfileResponse::fromEntity)
                .collect(Collectors.toList());
    }
}

