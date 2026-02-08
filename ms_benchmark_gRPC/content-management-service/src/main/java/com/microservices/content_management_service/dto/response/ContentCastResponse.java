package com.microservices.content_management_service.dto.response;

import com.microservices.content_management_service.entity.ContentCast;
import com.microservices.content_management_service.entity.CastCrew.RoleType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Content Cast Response DTO
 * İçerik-cast ilişkisi bilgilerini döndürme yanıtı
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ContentCastResponse {
    
    private Long id;
    private Long castCrewId;
    private String castCrewName;
    private String characterName;
    private RoleType roleType;
    private String profileImageUrl;
    
    /**
     * Entity'den Response DTO'ya dönüşüm
     */
    public static ContentCastResponse fromEntity(ContentCast contentCast) {
        ContentCastResponse.ContentCastResponseBuilder builder = ContentCastResponse.builder()
                .id(contentCast.getId())
                .characterName(contentCast.getCharacterName())
                .roleType(contentCast.getRoleType());
        
        if (contentCast.getCastCrew() != null) {
            builder.castCrewId(contentCast.getCastCrew().getId())
                   .castCrewName(contentCast.getCastCrew().getName())
                   .profileImageUrl(contentCast.getCastCrew().getProfileImageUrl());
        }
        
        return builder.build();
    }
}

