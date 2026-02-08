package com.microservices.subscription_and_billing_service.controller;

import com.microservices.subscription_and_billing_service.dto.response.SubscriptionPlanResponse;
import com.microservices.subscription_and_billing_service.service.SubscriptionPlanService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * SubscriptionPlan Controller
 * Abonelik planlarını listeler
 */
@RestController
@RequestMapping("/api/subscription")
public class SubscriptionPlanController {

    private static final Logger log = LoggerFactory.getLogger(SubscriptionPlanController.class);

    private final SubscriptionPlanService subscriptionPlanService;

    public SubscriptionPlanController(SubscriptionPlanService subscriptionPlanService) {
        this.subscriptionPlanService = subscriptionPlanService;
    }

    /**
     * Tüm aktif abonelik planlarını listele
     * PUBLIC ENDPOINT - Token gerektirmez
     * 
     * GET /api/subscription/plans
     */
    @GetMapping("/plans")
    public ResponseEntity<List<SubscriptionPlanResponse>> getAllPlans() {
        log.info("Received request to get all subscription plans");
        
        List<SubscriptionPlanResponse> plans = subscriptionPlanService.getAllActivePlans();
        
        return ResponseEntity.ok(plans);
    }

    /**
     * Health check endpoint
     * PUBLIC ENDPOINT - Token gerektirmez
     * 
     * GET /api/subscription/health
     */
    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Subscription and Billing Service is running");
    }
}




