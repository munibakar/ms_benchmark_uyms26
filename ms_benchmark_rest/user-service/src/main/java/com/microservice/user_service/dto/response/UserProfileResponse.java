package com.microservice.user_service.dto.response;

import com.microservice.user_service.entity.UserProfile;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * User Profile Response DTO
 * Kullanıcı profil bilgilerini döndürme yanıtı
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserProfileResponse {
    
    private Long id;
    private String userId;
    private String email;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private LocalDate dateOfBirth;
    private String profilePictureUrl;
    private String bio;
    private String country;
    private String city;
    private String address;
    private String postalCode;
    private Boolean isActive;
    private Boolean isVerified;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    /**
     * Entity'den Response DTO'ya dönüşüm
     */
    public static UserProfileResponse fromEntity(UserProfile userProfile) {
        return UserProfileResponse.builder()
                .id(userProfile.getId())
                .userId(userProfile.getUserId())
                .email(userProfile.getEmail())
                .firstName(userProfile.getFirstName())
                .lastName(userProfile.getLastName())
                .phoneNumber(userProfile.getPhoneNumber())
                .dateOfBirth(userProfile.getDateOfBirth())
                .profilePictureUrl(userProfile.getProfilePictureUrl())
                .bio(userProfile.getBio())
                .country(userProfile.getCountry())
                .city(userProfile.getCity())
                .address(userProfile.getAddress())
                .postalCode(userProfile.getPostalCode())
                .isActive(userProfile.getIsActive())
                .isVerified(userProfile.getIsVerified())
                .createdAt(userProfile.getCreatedAt())
                .updatedAt(userProfile.getUpdatedAt())
                .build();
    }
}

