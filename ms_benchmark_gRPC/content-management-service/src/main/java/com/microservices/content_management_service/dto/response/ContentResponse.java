package com.microservices.content_management_service.dto.response;

import com.microservices.content_management_service.entity.Content;
import com.microservices.content_management_service.entity.Content.ContentStatus;
import com.microservices.content_management_service.entity.Content.ContentType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Content Response DTO
 * İçerik bilgilerini döndürme yanıtı
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ContentResponse {
    
    private Long id;
    private String title;
    private String description;
    private ContentType contentType;
    private Integer releaseYear;
    private Integer durationMinutes;
    private String videoFilePath;
    private String posterUrl;
    private String thumbnailUrl;
    private String trailerUrl;
    private Double rating;
    private String ageRating;
    private String language;
    private ContentStatus status;
    private Boolean isFeatured;
    private Long viewCount;
    private Integer totalSeasons;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // İlişkiler
    private List<GenreResponse> genres;
    private List<ContentCastResponse> castCrew;
    private List<SeasonResponse> seasons;
    
    /**
     * Entity'den Response DTO'ya dönüşüm
     */
    public static ContentResponse fromEntity(Content content) {
        ContentResponse.ContentResponseBuilder builder = ContentResponse.builder()
                .id(content.getId())
                .title(content.getTitle())
                .description(content.getDescription())
                .contentType(content.getContentType())
                .releaseYear(content.getReleaseYear())
                .durationMinutes(content.getDurationMinutes())
                .videoFilePath(content.getVideoFilePath())
                .posterUrl(content.getPosterUrl())
                .thumbnailUrl(content.getThumbnailUrl())
                .trailerUrl(content.getTrailerUrl())
                .rating(content.getRating())
                .ageRating(content.getAgeRating())
                .language(content.getLanguage())
                .status(content.getStatus())
                .isFeatured(content.getIsFeatured())
                .viewCount(content.getViewCount())
                .totalSeasons(content.getTotalSeasons())
                .isActive(content.getIsActive())
                .createdAt(content.getCreatedAt())
                .updatedAt(content.getUpdatedAt());
        
        // Genres
        if (content.getContentGenres() != null && !content.getContentGenres().isEmpty()) {
            builder.genres(content.getContentGenres().stream()
                    .map(cg -> GenreResponse.fromEntity(cg.getGenre()))
                    .collect(Collectors.toList()));
        }
        
        // Cast/Crew
        if (content.getContentCasts() != null && !content.getContentCasts().isEmpty()) {
            builder.castCrew(content.getContentCasts().stream()
                    .map(ContentCastResponse::fromEntity)
                    .collect(Collectors.toList()));
        }
        
        // Seasons
        if (content.getSeasons() != null && !content.getSeasons().isEmpty()) {
            builder.seasons(content.getSeasons().stream()
                    .map(SeasonResponse::fromEntity)
                    .collect(Collectors.toList()));
        }
        
        return builder.build();
    }
}






