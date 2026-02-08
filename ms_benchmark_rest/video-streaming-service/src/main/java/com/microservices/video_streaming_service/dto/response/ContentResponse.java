package com.microservices.video_streaming_service.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Content Response DTO
 * Content Management Service'ten gelen içerik bilgilerini temsil eder
 * Sadece video streaming için gerekli alanları içerir
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ContentResponse {
    
    private Long id;
    private String title;
    private String videoFilePath;
    private String contentType;  // MOVIE, TV_SERIES gibi
    private Boolean isActive;
    
    // Diğer alanlar JSON'dan gelirse ignore edilir (Lombok @Data otomatik handle eder)
}

