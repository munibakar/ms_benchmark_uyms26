package com.microservices.profile_service.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * User Profile Response DTO
 * User Service'ten gelen kullanıcı profil bilgisi
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserProfileResponse {
    
    private String id;
    private String userId;
    private String email;
    private String firstName;
    private String lastName;
    private Boolean isActive;
    private Boolean isVerified;
}
