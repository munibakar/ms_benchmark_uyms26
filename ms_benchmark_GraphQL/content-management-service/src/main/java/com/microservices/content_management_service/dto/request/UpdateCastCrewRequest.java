package com.microservices.content_management_service.dto.request;

import com.microservices.content_management_service.entity.CastCrew.RoleType;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Update CastCrew Request DTO
 * Cast/crew güncelleme isteği
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateCastCrewRequest {
    
    @Size(min = 1, max = 255, message = "Name must be between 1 and 255 characters")
    private String name;
    
    private String biography;
    
    private String profileImageUrl;
    
    private RoleType roleType;
    
    private LocalDateTime dateOfBirth;
    
    private String nationality;
}






