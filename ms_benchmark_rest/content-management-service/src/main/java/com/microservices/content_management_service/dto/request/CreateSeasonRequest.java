package com.microservices.content_management_service.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Create Season Request DTO
 * Yeni sezon oluşturma isteği
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateSeasonRequest {
    
    @NotNull(message = "Content ID is required")
    private Long contentId;
    
    @NotNull(message = "Season number is required")
    private Integer seasonNumber;
    
    @Size(max = 255, message = "Title must be maximum 255 characters")
    private String title;
    
    private String description;
    
    private Integer releaseYear;
    
    private String posterUrl;
}






