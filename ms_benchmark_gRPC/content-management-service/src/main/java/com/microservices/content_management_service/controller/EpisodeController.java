package com.microservices.content_management_service.controller;

import com.microservices.content_management_service.dto.response.EpisodeResponse;
import com.microservices.content_management_service.service.EpisodeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Episode Controller - Content Management Service
 * Bölüm okuma işlemlerini yöneten REST API (Read-only)
 * Kullanıcılar sadece bölüm bilgilerini görüntüleyebilir, ekleme/silme yapamaz
 */
@RestController
@RequestMapping("/api/episodes")
public class EpisodeController {

    private static final Logger log = LoggerFactory.getLogger(EpisodeController.class);

    private final EpisodeService episodeService;

    public EpisodeController(EpisodeService episodeService) {
        this.episodeService = episodeService;
    }

    /**
     * Season ID'ye göre aktif bölümleri getir
     * GET /api/episodes/season/{seasonId}
     */
    @GetMapping("/season/{seasonId}")
    public ResponseEntity<List<EpisodeResponse>> getActiveEpisodesBySeasonId(@PathVariable Long seasonId) {
        log.info("Received request to get active episodes for seasonId: {}", seasonId);
        
        List<EpisodeResponse> response = episodeService.getActiveEpisodesBySeasonId(seasonId);
        
        return ResponseEntity.ok(response);
    }

    /**
     * Episode ID'ye göre bölüm getir
     * GET /api/episodes/{episodeId}
     */
    @GetMapping("/{episodeId}")
    public ResponseEntity<EpisodeResponse> getEpisodeById(@PathVariable Long episodeId) {
        log.info("Received request to get episode for episodeId: {}", episodeId);
        
        EpisodeResponse response = episodeService.getEpisodeById(episodeId);
        
        return ResponseEntity.ok(response);
    }
}

