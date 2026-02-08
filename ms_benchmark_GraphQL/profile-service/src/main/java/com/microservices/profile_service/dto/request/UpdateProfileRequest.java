package com.microservices.profile_service.dto.request;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Update Profile Request DTO
 * Profil güncelleme isteği
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateProfileRequest {
    
    @Size(min = 1, max = 50, message = "Profile name must be between 1 and 50 characters")
    private String profileName;
    
    private String avatarUrl;
    
    private Boolean isChildProfile;
    
    private String maturityLevel; // ALL, PG, PG13, R, NC17
    
    private String language; // tr, en, fr, de, etc.
    
    private Boolean isPinProtected;
    
    private String pin; // PIN (şifrelenmemiş, service'te hash'lenecek)
    
    private Boolean isDefault;
}
