package com.microservices.content_management_service.dto.request;

import com.microservices.content_management_service.entity.CastCrew.RoleType;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Content Cast Request DTO
 * İçerik-cast ilişkisi için istek
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ContentCastRequest {
    
    @NotNull(message = "Cast/Crew ID is required")
    private Long castCrewId;
    
    private String characterName; // Oyuncunun canlandırdığı karakter adı
    
    @NotNull(message = "Role type is required")
    private RoleType roleType; // ACTOR, DIRECTOR, WRITER, PRODUCER
}






