package com.microservices.subscription_and_billing_service.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Health Controller - Subscription and Billing Service
 * Docker/Kubernetes health check endpoint
 */
@RestController
@RequestMapping("/api/subscription")
public class HealthController {

    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Subscription and Billing Service is running");
    }
}
