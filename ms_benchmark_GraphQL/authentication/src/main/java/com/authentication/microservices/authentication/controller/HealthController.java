package com.authentication.microservices.authentication.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Health Controller - Authentication Service
 * Docker/Kubernetes health check endpoint
 */
@RestController
@RequestMapping("/api/auth")
public class HealthController {

    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Authentication Service is running");
    }
}
