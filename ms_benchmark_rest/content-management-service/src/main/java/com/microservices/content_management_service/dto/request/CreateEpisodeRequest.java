package com.microservices.content_management_service.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Create Episode Request DTO
 * Yeni bölüm oluşturma isteği
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateEpisodeRequest {
    
    @NotNull(message = "Season ID is required")
    private Long seasonId;
    
    @NotNull(message = "Episode number is required")
    private Integer episodeNumber;
    
    @NotBlank(message = "Title is required")
    @Size(min = 1, max = 255, message = "Title must be between 1 and 255 characters")
    private String title;
    
    private String description;
    
    private Integer durationMinutes;
    
    @NotBlank(message = "Video file path is required")
    private String videoFilePath; // Local dosya yolu
    
    private String thumbnailUrl;
    
    private LocalDateTime releaseDate;
}






