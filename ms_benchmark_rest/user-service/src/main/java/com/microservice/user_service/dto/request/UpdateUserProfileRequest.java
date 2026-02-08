package com.microservice.user_service.dto.request;

import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * Update User Profile Request DTO
 * Kullanıcı profil bilgilerini güncelleme isteği
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateUserProfileRequest {
    
    @Email(message = "Email must be valid")
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
}

