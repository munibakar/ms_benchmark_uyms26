package com.authentication.microservices.authentication.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Create User Profile Request DTO
 * User Service'e kullanıcı profili oluşturma isteği göndermek için
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateUserProfileRequest {
    
    private String userId;
    private String email;
    private String firstName;
    private String lastName;
}

