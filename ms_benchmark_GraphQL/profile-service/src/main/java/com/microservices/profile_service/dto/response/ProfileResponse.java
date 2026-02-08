package com.microservices.profile_service.dto.response;

import com.microservices.profile_service.entity.Profile;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Profile Response DTO
 * Profil bilgilerini döndürme yanıtı
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProfileResponse {
    
    private Long id;
    private String accountId;
    private String profileName;
    private String avatarUrl;
    private Boolean isChildProfile;
    private String maturityLevel;
    private String language;
    private Boolean isPinProtected;
    private Boolean isActive;
    private Boolean isDefault;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    /**
     * Entity'den Response DTO'ya dönüşüm
     */
    public static ProfileResponse fromEntity(Profile profile) {
        return ProfileResponse.builder()
                .id(profile.getId())
                .accountId(profile.getAccountId())
                .profileName(profile.getProfileName())
                .avatarUrl(profile.getAvatarUrl())
                .isChildProfile(profile.getIsChildProfile())
                .maturityLevel(profile.getMaturityLevel())
                .language(profile.getLanguage())
                .isPinProtected(profile.getIsPinProtected())
                .isActive(profile.getIsActive())
                .isDefault(profile.getIsDefault())
                .createdAt(profile.getCreatedAt())
                .updatedAt(profile.getUpdatedAt())
                .build();
    }
}
