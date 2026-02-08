package com.microservices.content_management_service.controller;

import com.microservices.content_management_service.dto.response.ContentResponse;
import com.microservices.content_management_service.entity.Content.ContentStatus;
import com.microservices.content_management_service.entity.Content.ContentType;
import com.microservices.content_management_service.service.ContentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Content Controller - Content Management Service
 * İçerik okuma işlemlerini yöneten REST API (Read-only)
 * Kullanıcılar sadece içerikleri görüntüleyebilir, ekleme/güncelleme/silme yapamaz
 */
@RestController
@RequestMapping("/api/contents")
public class ContentController {

    private static final Logger log = LoggerFactory.getLogger(ContentController.class);

    private final ContentService contentService;

    public ContentController(ContentService contentService) {
        this.contentService = contentService;
    }

    /**
     * Health check endpoint
     * GET /api/contents/health
     */
    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Content Management Service is running");
    }

    /**
     * Tüm aktif içerikleri getir
     * GET /api/contents
     */
    @GetMapping
    public ResponseEntity<List<ContentResponse>> getAllActiveContents() {
        log.info("Received request to get all active contents");
        
        List<ContentResponse> response = contentService.getAllActiveContents();
        
        return ResponseEntity.ok(response);
    }

    /**
     * Content ID'ye göre içerik getir
     * GET /api/contents/{contentId}
     */
    @GetMapping("/{contentId}")
    public ResponseEntity<ContentResponse> getContentById(@PathVariable Long contentId) {
        log.info("Received request to get content for contentId: {}", contentId);
        
        ContentResponse response = contentService.getContentById(contentId);
        
        return ResponseEntity.ok(response);
    }

    /**
     * Content type'a göre içerikleri getir
     * GET /api/contents/type/{contentType}
     */
    @GetMapping("/type/{contentType}")
    public ResponseEntity<List<ContentResponse>> getContentsByType(@PathVariable ContentType contentType) {
        log.info("Received request to get contents for type: {}", contentType);
        
        List<ContentResponse> response = contentService.getContentsByType(contentType);
        
        return ResponseEntity.ok(response);
    }

    /**
     * Status'e göre içerikleri getir
     * GET /api/contents/status/{status}
     */
    @GetMapping("/status/{status}")
    public ResponseEntity<List<ContentResponse>> getContentsByStatus(@PathVariable ContentStatus status) {
        log.info("Received request to get contents for status: {}", status);
        
        List<ContentResponse> response = contentService.getContentsByStatus(status);
        
        return ResponseEntity.ok(response);
    }

    /**
     * Öne çıkan içerikleri getir
     * GET /api/contents/featured
     */
    @GetMapping("/featured")
    public ResponseEntity<List<ContentResponse>> getFeaturedContents() {
        log.info("Received request to get featured contents");
        
        List<ContentResponse> response = contentService.getFeaturedContents();
        
        return ResponseEntity.ok(response);
    }

    /**
     * Title'a göre arama
     * GET /api/contents/search?title={title}
     */
    @GetMapping("/search")
    public ResponseEntity<List<ContentResponse>> searchContentsByTitle(@RequestParam String title) {
        log.info("Received request to search contents by title: {}", title);
        
        List<ContentResponse> response = contentService.searchContentsByTitle(title);
        
        return ResponseEntity.ok(response);
    }
}

