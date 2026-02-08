package com.microservice.user_service.controller;

import com.microservice.user_service.dto.request.CreateUserProfileRequest;
import com.microservice.user_service.dto.request.UpdateUserProfileRequest;
import com.microservice.user_service.dto.response.UserProfileResponse;
import com.microservice.user_service.service.UserProfileService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * User Profile Controller - User Service
 * Kullanıcı profil işlemlerini yöneten REST API
 */
@RestController
@RequestMapping("/api/users")
public class UserProfileController {

    private static final Logger log = LoggerFactory.getLogger(UserProfileController.class);

    private final UserProfileService userProfileService;

    public UserProfileController(UserProfileService userProfileService) {
        this.userProfileService = userProfileService;
    }

    /**
     * Yeni kullanıcı profili oluştur
     * Bu endpoint Auth Service tarafından OpenFeign ile çağrılır
     * 
     * POST /api/users/profile
     */
    @PostMapping("/profile")
    public ResponseEntity<UserProfileResponse> createUserProfile(
            @Valid @RequestBody CreateUserProfileRequest request) {
        log.info("Received request to create user profile for userId: {}", request.getUserId());
        
        UserProfileResponse response = userProfileService.createUserProfile(request);
        
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    /**
     * Kullanıcı ID'sine göre profil getir
     * 
     * GET /api/users/profile/{userId}
     */
    @GetMapping("/profile/{userId}")
    public ResponseEntity<UserProfileResponse> getUserProfileByUserId(@PathVariable String userId) {
        log.info("Received request to get user profile for userId: {}", userId);
        
        UserProfileResponse response = userProfileService.getUserProfileByUserId(userId);
        
        return ResponseEntity.ok(response);
    }

    /**
     * Email'e göre profil getir
     * 
     * GET /api/users/profile/email/{email}
     */
    @GetMapping("/profile/email/{email}")
    public ResponseEntity<UserProfileResponse> getUserProfileByEmail(@PathVariable String email) {
        log.info("Received request to get user profile for email: {}", email);
        
        UserProfileResponse response = userProfileService.getUserProfileByEmail(email);
        
        return ResponseEntity.ok(response);
    }

    /**
     * Kullanıcı profilini güncelle
     * 
     * PUT /api/users/profile/{userId}
     */
    @PutMapping("/profile/{userId}")
    public ResponseEntity<UserProfileResponse> updateUserProfile(
            @PathVariable String userId,
            @Valid @RequestBody UpdateUserProfileRequest request) {
        log.info("Received request to update user profile for userId: {}", userId);
        
        UserProfileResponse response = userProfileService.updateUserProfile(userId, request);
        
        return ResponseEntity.ok(response);
    }

    /**
     * Kullanıcı profilini sil (soft delete)
     * 
     * DELETE /api/users/profile/{userId}
     */
    @DeleteMapping("/profile/{userId}")
    public ResponseEntity<Void> deleteUserProfile(@PathVariable String userId) {
        log.info("Received request to delete user profile for userId: {}", userId);
        
        userProfileService.deleteUserProfile(userId);
        
        return ResponseEntity.noContent().build();
    }

    /**
     * Tüm kullanıcı profillerini listele
     * 
     * GET /api/users/profiles
     */
    @GetMapping("/profiles")
    public ResponseEntity<List<UserProfileResponse>> getAllUserProfiles() {
        log.info("Received request to get all user profiles");
        
        List<UserProfileResponse> response = userProfileService.getAllUserProfiles();
        
        return ResponseEntity.ok(response);
    }

    /**
     * Health check endpoint
     * 
     * GET /api/users/health
     */
    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("User Service is running");
    }
}

