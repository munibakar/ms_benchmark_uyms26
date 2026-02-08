package com.microservices.content_management_service.dto.request;

import com.microservices.content_management_service.entity.Content.ContentStatus;
import com.microservices.content_management_service.entity.Content.ContentType;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Update Content Request DTO
 * İçerik güncelleme isteği
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateContentRequest {
    
    @Size(min = 1, max = 255, message = "Title must be between 1 and 255 characters")
    private String title;
    
    private String description;
    
    private ContentType contentType;
    
    private Integer releaseYear;
    
    private Integer durationMinutes;
    
    private String videoFilePath;
    
    private String posterUrl;
    
    private String thumbnailUrl;
    
    private String trailerUrl;
    
    private String ageRating;
    
    private String language;
    
    private ContentStatus status;
    
    private Boolean isFeatured;
    
    private Integer totalSeasons;
    
    // İlişkiler
    private List<Long> genreIds;
    
    private List<ContentCastRequest> castCrew;
}






