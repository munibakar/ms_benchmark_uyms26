package com.microservices.profile_service.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Health Controller - Profile Service
 * Docker/Kubernetes health check endpoint
 */
@RestController
@RequestMapping("/api/profiles")
public class HealthController {

    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Profile Service is running");
    }
}
