package com.microservices.subscription_and_billing_service.controller;

import com.microservices.subscription_and_billing_service.dto.response.BillingHistoryResponse;
import com.microservices.subscription_and_billing_service.service.BillingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Billing Controller
 * Fatura ve ödeme geçmişi işlemleri
 */
@RestController
@RequestMapping("/api/billing")
public class BillingController {

    private static final Logger log = LoggerFactory.getLogger(BillingController.class);

    private final BillingService billingService;

    public BillingController(BillingService billingService) {
        this.billingService = billingService;
    }

    /**
     * Kullanıcının fatura geçmişini getir
     * PROTECTED ENDPOINT - JWT token gerektirir
     * 
     * GET /api/billing/history
     */
    @GetMapping("/history")
    public ResponseEntity<List<BillingHistoryResponse>> getBillingHistory(
            @RequestHeader("X-User-Id") String userId) {
        log.info("Received request to get billing history for userId: {}", userId);
        
        List<BillingHistoryResponse> billingHistory = billingService.getBillingHistory(userId);
        
        return ResponseEntity.ok(billingHistory);
    }

    /**
     * Başarılı ödemeleri getir
     * PROTECTED ENDPOINT - JWT token gerektirir
     * 
     * GET /api/billing/successful-payments
     */
    @GetMapping("/successful-payments")
    public ResponseEntity<List<BillingHistoryResponse>> getSuccessfulPayments(
            @RequestHeader("X-User-Id") String userId) {
        log.info("Received request to get successful payments for userId: {}", userId);
        
        List<BillingHistoryResponse> payments = billingService.getSuccessfulPayments(userId);
        
        return ResponseEntity.ok(payments);
    }
}




