package com.microservices.subscription_and_billing_service.controller;

import com.microservices.subscription_and_billing_service.dto.request.CancelSubscriptionRequest;
import com.microservices.subscription_and_billing_service.dto.request.SubscribeRequest;
import com.microservices.subscription_and_billing_service.dto.response.SubscriptionResponse;
import com.microservices.subscription_and_billing_service.service.SubscriptionService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Subscription Controller
 * Kullanıcı abonelik işlemleri
 */
@RestController
@RequestMapping("/api/subscription")
public class SubscriptionController {

    private static final Logger log = LoggerFactory.getLogger(SubscriptionController.class);

    private final SubscriptionService subscriptionService;

    public SubscriptionController(SubscriptionService subscriptionService) {
        this.subscriptionService = subscriptionService;
    }

    /**
     * Kullanıcının aktif aboneliğini getir
     * PROTECTED ENDPOINT - JWT token gerektirir
     * 
     * GET /api/subscription/my-subscription
     */
    @GetMapping("/my-subscription")
    public ResponseEntity<SubscriptionResponse> getMySubscription(
            @RequestHeader("X-User-Id") String userId) {
        log.info("Received request to get active subscription for userId: {}", userId);
        
        SubscriptionResponse subscription = subscriptionService.getActiveSubscription(userId);
        
        return ResponseEntity.ok(subscription);
    }

    /**
     * Kullanıcının tüm aboneliklerini getir (geçmiş dahil)
     * PROTECTED ENDPOINT - JWT token gerektirir
     * 
     * GET /api/subscription/my-subscriptions
     */
    @GetMapping("/my-subscriptions")
    public ResponseEntity<List<SubscriptionResponse>> getMySubscriptions(
            @RequestHeader("X-User-Id") String userId) {
        log.info("Received request to get all subscriptions for userId: {}", userId);
        
        List<SubscriptionResponse> subscriptions = subscriptionService.getAllSubscriptions(userId);
        
        return ResponseEntity.ok(subscriptions);
    }

    /**
     * Yeni abonelik satın al
     * PROTECTED ENDPOINT - JWT token gerektirir
     * 
     * POST /api/subscription/subscribe
     */
    @PostMapping("/subscribe")
    public ResponseEntity<SubscriptionResponse> subscribe(
            @RequestHeader("X-User-Id") String userId,
            @Valid @RequestBody SubscribeRequest request) {
        log.info("Received request to create subscription for userId: {} with plan: {}", 
                userId, request.getPlanName());
        
        SubscriptionResponse subscription = subscriptionService.subscribe(userId, request);
        
        return new ResponseEntity<>(subscription, HttpStatus.CREATED);
    }

    /**
     * Aboneliği iptal et
     * PROTECTED ENDPOINT - JWT token gerektirir
     * 
     * PUT /api/subscription/cancel
     */
    @PutMapping("/cancel")
    public ResponseEntity<SubscriptionResponse> cancelSubscription(
            @RequestHeader("X-User-Id") String userId,
            @RequestBody CancelSubscriptionRequest request) {
        log.info("Received request to cancel subscription for userId: {}", userId);
        
        SubscriptionResponse subscription = subscriptionService.cancelSubscription(userId, request);
        
        return ResponseEntity.ok(subscription);
    }
}




