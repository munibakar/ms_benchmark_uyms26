package com.microservices.content_management_service.dto.response;

import com.microservices.content_management_service.entity.CastCrew;
import com.microservices.content_management_service.entity.CastCrew.RoleType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * CastCrew Response DTO
 * Cast/Crew bilgilerini döndürme yanıtı
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CastCrewResponse {
    
    private Long id;
    private String name;
    private String biography;
    private String profileImageUrl;
    private RoleType roleType;
    private LocalDateTime dateOfBirth;
    private String nationality;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    /**
     * Entity'den Response DTO'ya dönüşüm
     */
    public static CastCrewResponse fromEntity(CastCrew castCrew) {
        return CastCrewResponse.builder()
                .id(castCrew.getId())
                .name(castCrew.getName())
                .biography(castCrew.getBiography())
                .profileImageUrl(castCrew.getProfileImageUrl())
                .roleType(castCrew.getRoleType())
                .dateOfBirth(castCrew.getDateOfBirth())
                .nationality(castCrew.getNationality())
                .isActive(castCrew.getIsActive())
                .createdAt(castCrew.getCreatedAt())
                .updatedAt(castCrew.getUpdatedAt())
                .build();
    }
}






