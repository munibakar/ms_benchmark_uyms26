package com.microservices.content_management_service.controller;

import com.microservices.content_management_service.dto.response.CastCrewResponse;
import com.microservices.content_management_service.entity.CastCrew.RoleType;
import com.microservices.content_management_service.service.CastCrewService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * CastCrew Controller - Content Management Service
 * Cast/Crew okuma işlemlerini yöneten REST API (Read-only)
 * Kullanıcılar sadece cast/crew bilgilerini görüntüleyebilir, ekleme/güncelleme/silme yapamaz
 */
@RestController
@RequestMapping("/api/cast-crew")
public class CastCrewController {

    private static final Logger log = LoggerFactory.getLogger(CastCrewController.class);

    private final CastCrewService castCrewService;

    public CastCrewController(CastCrewService castCrewService) {
        this.castCrewService = castCrewService;
    }

    /**
     * Tüm aktif cast/crew'leri getir
     * GET /api/cast-crew
     */
    @GetMapping
    public ResponseEntity<List<CastCrewResponse>> getAllActiveCastCrew() {
        log.info("Received request to get all active cast/crew");
        
        List<CastCrewResponse> response = castCrewService.getAllActiveCastCrew();
        
        return ResponseEntity.ok(response);
    }

    /**
     * Role type'a göre cast/crew'leri getir
     * GET /api/cast-crew/role/{roleType}
     */
    @GetMapping("/role/{roleType}")
    public ResponseEntity<List<CastCrewResponse>> getCastCrewByRoleType(@PathVariable RoleType roleType) {
        log.info("Received request to get cast/crew for roleType: {}", roleType);
        
        List<CastCrewResponse> response = castCrewService.getCastCrewByRoleType(roleType);
        
        return ResponseEntity.ok(response);
    }

    /**
     * CastCrew ID'ye göre cast/crew getir
     * GET /api/cast-crew/{castCrewId}
     */
    @GetMapping("/{castCrewId}")
    public ResponseEntity<CastCrewResponse> getCastCrewById(@PathVariable Long castCrewId) {
        log.info("Received request to get cast/crew for castCrewId: {}", castCrewId);
        
        CastCrewResponse response = castCrewService.getCastCrewById(castCrewId);
        
        return ResponseEntity.ok(response);
    }

    /**
     * İsme göre arama
     * GET /api/cast-crew/search?name={name}
     */
    @GetMapping("/search")
    public ResponseEntity<List<CastCrewResponse>> searchCastCrewByName(@RequestParam String name) {
        log.info("Received request to search cast/crew by name: {}", name);
        
        List<CastCrewResponse> response = castCrewService.searchCastCrewByName(name);
        
        return ResponseEntity.ok(response);
    }
}

