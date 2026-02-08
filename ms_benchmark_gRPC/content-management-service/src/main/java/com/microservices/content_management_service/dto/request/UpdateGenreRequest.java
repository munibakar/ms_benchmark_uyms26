package com.microservices.content_management_service.dto.request;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Update Genre Request DTO
 * Tür güncelleme isteği
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateGenreRequest {
    
    @Size(min = 1, max = 100, message = "Genre name must be between 1 and 100 characters")
    private String name;
    
    private String description;
}






