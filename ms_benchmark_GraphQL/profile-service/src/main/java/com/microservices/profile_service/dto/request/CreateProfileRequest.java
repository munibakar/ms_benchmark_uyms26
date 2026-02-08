package com.microservices.profile_service.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Create Profile Request DTO
 * Yeni profil oluşturma isteği
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateProfileRequest {
    
    @NotNull(message = "Account ID is required")
    private String accountId;
    
    @NotBlank(message = "Profile name is required")
    @Size(min = 1, max = 50, message = "Profile name must be between 1 and 50 characters")
    private String profileName;
    
    private String avatarUrl;
    
    @Builder.Default
    private Boolean isChildProfile = false;
    
    @Builder.Default
    private String maturityLevel = "ALL"; // ALL, PG, PG13, R, NC17
    
    @Builder.Default
    private String language = "tr"; // tr, en, fr, de, etc.
    
    @Builder.Default
    private Boolean isPinProtected = false;
    
    private String pin; // PIN (şifrelenmemiş, service'te hash'lenecek)
    
    @Builder.Default
    private Boolean isDefault = false;
}
