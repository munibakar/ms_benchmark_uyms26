package com.microservices.content_management_service.dto.response;

import com.microservices.content_management_service.entity.Season;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Season Response DTO
 * Sezon bilgilerini döndürme yanıtı
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SeasonResponse {
    
    private Long id;
    private Long contentId;
    private Integer seasonNumber;
    private String title;
    private String description;
    private Integer releaseYear;
    private String posterUrl;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // İlişkiler
    private List<EpisodeResponse> episodes;
    
    /**
     * Entity'den Response DTO'ya dönüşüm
     */
    public static SeasonResponse fromEntity(Season season) {
        SeasonResponse.SeasonResponseBuilder builder = SeasonResponse.builder()
                .id(season.getId())
                .contentId(season.getContent() != null ? season.getContent().getId() : null)
                .seasonNumber(season.getSeasonNumber())
                .title(season.getTitle())
                .description(season.getDescription())
                .releaseYear(season.getReleaseYear())
                .posterUrl(season.getPosterUrl())
                .isActive(season.getIsActive())
                .createdAt(season.getCreatedAt())
                .updatedAt(season.getUpdatedAt());
        
        // Episodes
        if (season.getEpisodes() != null && !season.getEpisodes().isEmpty()) {
            builder.episodes(season.getEpisodes().stream()
                    .map(EpisodeResponse::fromEntity)
                    .collect(Collectors.toList()));
        }
        
        return builder.build();
    }
}

