package com.microservices.content_management_service.dto.request;

import com.microservices.content_management_service.entity.Content.ContentStatus;
import com.microservices.content_management_service.entity.Content.ContentType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Create Content Request DTO
 * Yeni içerik oluşturma isteği
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateContentRequest {
    
    @NotBlank(message = "Title is required")
    @Size(min = 1, max = 255, message = "Title must be between 1 and 255 characters")
    private String title;
    
    private String description;
    
    @NotNull(message = "Content type is required")
    private ContentType contentType; // MOVIE, TV_SERIES
    
    private Integer releaseYear;
    
    private Integer durationMinutes; // Film için toplam süre, dizi için bölüm başına ortalama süre
    
    @NotBlank(message = "Video file path is required")
    private String videoFilePath; // Local dosya yolu
    
    private String posterUrl;
    
    private String thumbnailUrl;
    
    private String trailerUrl;
    
    private String ageRating; // ALL, PG, PG13, R, NC17
    
    @Builder.Default
    private String language = "tr"; // tr, en, fr, de, etc.
    
    @Builder.Default
    private ContentStatus status = ContentStatus.DRAFT; // DRAFT, PUBLISHED, COMING_SOON, ARCHIVED
    
    @Builder.Default
    private Boolean isFeatured = false;
    
    private Integer totalSeasons; // Sadece TV_SERIES için
    
    // İlişkiler
    private List<Long> genreIds; // Genre ID'leri
    
    private List<ContentCastRequest> castCrew; // Cast/Crew bilgileri
}






