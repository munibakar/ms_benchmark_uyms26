package com.microservices.content_management_service.dto.response;

import com.microservices.content_management_service.entity.Genre;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Genre Response DTO
 * Tür bilgilerini döndürme yanıtı
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GenreResponse {
    
    private Long id;
    private String name;
    private String description;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    /**
     * Entity'den Response DTO'ya dönüşüm
     */
    public static GenreResponse fromEntity(Genre genre) {
        return GenreResponse.builder()
                .id(genre.getId())
                .name(genre.getName())
                .description(genre.getDescription())
                .isActive(genre.getIsActive())
                .createdAt(genre.getCreatedAt())
                .updatedAt(genre.getUpdatedAt())
                .build();
    }
}






