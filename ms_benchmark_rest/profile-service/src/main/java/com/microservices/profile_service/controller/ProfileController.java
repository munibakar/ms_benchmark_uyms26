package com.microservices.profile_service.controller;

import com.microservices.profile_service.dto.request.CreateProfileRequest;
import com.microservices.profile_service.dto.request.UpdateProfileRequest;
import com.microservices.profile_service.dto.response.ProfileResponse;
import com.microservices.profile_service.service.ProfileService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Profile Controller - Profile Service
 * Profil işlemlerini yöneten REST API
 */
@RestController
@RequestMapping("/api/profiles")
public class ProfileController {

    private static final Logger log = LoggerFactory.getLogger(ProfileController.class);

    private final ProfileService profileService;

    public ProfileController(ProfileService profileService) {
        this.profileService = profileService;
    }

    /**
     * Yeni profil oluştur
     * 
     * POST /api/profiles
     */
    @PostMapping
    public ResponseEntity<ProfileResponse> createProfile(
            @Valid @RequestBody CreateProfileRequest request) {
        log.info("Received request to create profile for accountId: {}", request.getAccountId());
        
        ProfileResponse response = profileService.createProfile(request);
        
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    /**
     * Account ID'ye göre aktif profilleri getir
     * 
     * GET /api/profiles/account/{accountId}
     */
    @GetMapping("/account/{accountId}")
    public ResponseEntity<List<ProfileResponse>> getActiveProfilesByAccountId(
            @PathVariable String accountId) {
        log.info("Received request to get active profiles for accountId: {}", accountId);
        
        List<ProfileResponse> response = profileService.getActiveProfilesByAccountId(accountId);
        
        return ResponseEntity.ok(response);
    }

    /**
     * Profile ID'ye göre profil getir
     * 
     * GET /api/profiles/{profileId}
     */
    @GetMapping("/{profileId}")
    public ResponseEntity<ProfileResponse> getProfileById(@PathVariable Long profileId) {
        log.info("Received request to get profile for profileId: {}", profileId);
        
        ProfileResponse response = profileService.getProfileById(profileId);
        
        return ResponseEntity.ok(response);
    }

    /**
     * Account ID ve Profile ID'ye göre profil getir
     * 
     * GET /api/profiles/{profileId}/account/{accountId}
     */
    @GetMapping("/{profileId}/account/{accountId}")
    public ResponseEntity<ProfileResponse> getProfileByIdAndAccountId(
            @PathVariable Long profileId,
            @PathVariable String accountId) {
        log.info("Received request to get profile for profileId: {} and accountId: {}", 
                profileId, accountId);
        
        ProfileResponse response = profileService.getProfileByIdAndAccountId(profileId, accountId);
        
        return ResponseEntity.ok(response);
    }

    /**
     * Profil güncelle
     * 
     * PUT /api/profiles/{profileId}/account/{accountId}
     */
    @PutMapping("/{profileId}/account/{accountId}")
    public ResponseEntity<ProfileResponse> updateProfile(
            @PathVariable Long profileId,
            @PathVariable String accountId,
            @Valid @RequestBody UpdateProfileRequest request) {
        log.info("Received request to update profile for profileId: {} and accountId: {}", 
                profileId, accountId);
        
        ProfileResponse response = profileService.updateProfile(profileId, accountId, request);
        
        return ResponseEntity.ok(response);
    }

    /**
     * Profil sil (soft delete)
     * 
     * DELETE /api/profiles/{profileId}/account/{accountId}
     */
    @DeleteMapping("/{profileId}/account/{accountId}")
    public ResponseEntity<Void> deleteProfile(
            @PathVariable Long profileId,
            @PathVariable String accountId) {
        log.info("Received request to delete profile for profileId: {} and accountId: {}", 
                profileId, accountId);
        
        profileService.deleteProfile(profileId, accountId);
        
        return ResponseEntity.noContent().build();
    }

    /**
     * Varsayılan profili getir
     * 
     * GET /api/profiles/account/{accountId}/default
     */
    @GetMapping("/account/{accountId}/default")
    public ResponseEntity<ProfileResponse> getDefaultProfile(@PathVariable String accountId) {
        log.info("Received request to get default profile for accountId: {}", accountId);
        
        ProfileResponse response = profileService.getDefaultProfile(accountId);
        
        return ResponseEntity.ok(response);
    }

    /**
     * Profil sayısını getir
     * 
     * GET /api/profiles/account/{accountId}/count
     */
    @GetMapping("/account/{accountId}/count")
    public ResponseEntity<Long> getProfileCount(@PathVariable String accountId) {
        log.info("Received request to get profile count for accountId: {}", accountId);
        
        long count = profileService.getProfileCount(accountId);
        
        return ResponseEntity.ok(count);
    }

    /**
     * Health check endpoint
     * 
     * GET /api/profiles/health
     */
    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Profile Service is running");
    }
}
