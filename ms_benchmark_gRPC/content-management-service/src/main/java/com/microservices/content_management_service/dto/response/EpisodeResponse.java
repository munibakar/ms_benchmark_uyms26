package com.microservices.content_management_service.dto.response;

import com.microservices.content_management_service.entity.Episode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Episode Response DTO
 * Bölüm bilgilerini döndürme yanıtı
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EpisodeResponse {
    
    private Long id;
    private Long seasonId;
    private Integer episodeNumber;
    private String title;
    private String description;
    private Integer durationMinutes;
    private String videoFilePath;
    private String thumbnailUrl;
    private LocalDateTime releaseDate;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    /**
     * Entity'den Response DTO'ya dönüşüm
     */
    public static EpisodeResponse fromEntity(Episode episode) {
        return EpisodeResponse.builder()
                .id(episode.getId())
                .seasonId(episode.getSeason() != null ? episode.getSeason().getId() : null)
                .episodeNumber(episode.getEpisodeNumber())
                .title(episode.getTitle())
                .description(episode.getDescription())
                .durationMinutes(episode.getDurationMinutes())
                .videoFilePath(episode.getVideoFilePath())
                .thumbnailUrl(episode.getThumbnailUrl())
                .releaseDate(episode.getReleaseDate())
                .isActive(episode.getIsActive())
                .createdAt(episode.getCreatedAt())
                .updatedAt(episode.getUpdatedAt())
                .build();
    }
}

