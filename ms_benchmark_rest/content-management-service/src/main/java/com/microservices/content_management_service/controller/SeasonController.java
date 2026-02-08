package com.microservices.content_management_service.controller;

import com.microservices.content_management_service.dto.response.SeasonResponse;
import com.microservices.content_management_service.service.SeasonService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Season Controller - Content Management Service
 * Sezon okuma işlemlerini yöneten REST API (Read-only)
 * Kullanıcılar sadece sezon bilgilerini görüntüleyebilir, ekleme/silme yapamaz
 */
@RestController
@RequestMapping("/api/seasons")
public class SeasonController {

    private static final Logger log = LoggerFactory.getLogger(SeasonController.class);

    private final SeasonService seasonService;

    public SeasonController(SeasonService seasonService) {
        this.seasonService = seasonService;
    }

    /**
     * Content ID'ye göre aktif sezonları getir
     * GET /api/seasons/content/{contentId}
     */
    @GetMapping("/content/{contentId}")
    public ResponseEntity<List<SeasonResponse>> getActiveSeasonsByContentId(@PathVariable Long contentId) {
        log.info("Received request to get active seasons for contentId: {}", contentId);
        
        List<SeasonResponse> response = seasonService.getActiveSeasonsByContentId(contentId);
        
        return ResponseEntity.ok(response);
    }

    /**
     * Season ID'ye göre sezon getir
     * GET /api/seasons/{seasonId}
     */
    @GetMapping("/{seasonId}")
    public ResponseEntity<SeasonResponse> getSeasonById(@PathVariable Long seasonId) {
        log.info("Received request to get season for seasonId: {}", seasonId);
        
        SeasonResponse response = seasonService.getSeasonById(seasonId);
        
        return ResponseEntity.ok(response);
    }
}

