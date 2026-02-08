package com.microservices.content_management_service.controller;

import com.microservices.content_management_service.dto.response.GenreResponse;
import com.microservices.content_management_service.service.GenreService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Genre Controller - Content Management Service
 * Tür okuma işlemlerini yöneten REST API (Read-only)
 * Kullanıcılar sadece türleri görüntüleyebilir, ekleme/güncelleme/silme yapamaz
 */
@RestController
@RequestMapping("/api/genres")
public class GenreController {

    private static final Logger log = LoggerFactory.getLogger(GenreController.class);

    private final GenreService genreService;

    public GenreController(GenreService genreService) {
        this.genreService = genreService;
    }

    /**
     * Tüm aktif türleri getir
     * GET /api/genres
     */
    @GetMapping
    public ResponseEntity<List<GenreResponse>> getAllActiveGenres() {
        log.info("Received request to get all active genres");
        
        List<GenreResponse> response = genreService.getAllActiveGenres();
        
        return ResponseEntity.ok(response);
    }

    /**
     * Genre ID'ye göre tür getir
     * GET /api/genres/{genreId}
     */
    @GetMapping("/{genreId}")
    public ResponseEntity<GenreResponse> getGenreById(@PathVariable Long genreId) {
        log.info("Received request to get genre for genreId: {}", genreId);
        
        GenreResponse response = genreService.getGenreById(genreId);
        
        return ResponseEntity.ok(response);
    }

    /**
     * İsme göre arama
     * GET /api/genres/search?name={name}
     */
    @GetMapping("/search")
    public ResponseEntity<List<GenreResponse>> searchGenresByName(@RequestParam String name) {
        log.info("Received request to search genres by name: {}", name);
        
        List<GenreResponse> response = genreService.searchGenresByName(name);
        
        return ResponseEntity.ok(response);
    }
}

