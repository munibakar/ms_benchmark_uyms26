package com.microservice.user_service.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Create User Profile Request DTO
 * Auth Service'ten gelen kullanıcı profili oluşturma isteği
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateUserProfileRequest {
    
    @NotNull(message = "User ID is required")
    private String userId;
    
    @NotNull(message = "Email is required")
    @Email(message = "Email must be valid")
    private String email;
    
    private String firstName;
    
    private String lastName;
}

