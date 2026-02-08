package com.microservices.content_management_service.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Health Controller - Content Management Service
 * Docker/Kubernetes health check endpoint
 */
@RestController
@RequestMapping("/api/contents")
public class HealthController {

    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Content Management Service is running");
    }
}
