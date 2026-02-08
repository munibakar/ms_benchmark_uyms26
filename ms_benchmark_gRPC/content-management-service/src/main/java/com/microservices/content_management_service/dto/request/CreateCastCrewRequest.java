package com.microservices.content_management_service.dto.request;

import com.microservices.content_management_service.entity.CastCrew.RoleType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Create CastCrew Request DTO
 * Yeni cast/crew oluşturma isteği
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateCastCrewRequest {
    
    @NotBlank(message = "Name is required")
    @Size(min = 1, max = 255, message = "Name must be between 1 and 255 characters")
    private String name;
    
    private String biography;
    
    private String profileImageUrl;
    
    @NotNull(message = "Role type is required")
    private RoleType roleType; // ACTOR, DIRECTOR, WRITER, PRODUCER
    
    private LocalDateTime dateOfBirth;
    
    private String nationality;
}






