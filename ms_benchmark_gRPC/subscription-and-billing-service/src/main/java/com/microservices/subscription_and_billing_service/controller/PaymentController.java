package com.microservices.subscription_and_billing_service.controller;

import com.microservices.subscription_and_billing_service.dto.request.AddPaymentMethodRequest;
import com.microservices.subscription_and_billing_service.dto.response.PaymentMethodResponse;
import com.microservices.subscription_and_billing_service.service.PaymentService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Payment Controller
 * Ödeme yöntemleri işlemleri
 */
@RestController
@RequestMapping("/api/payment")
public class PaymentController {

    private static final Logger log = LoggerFactory.getLogger(PaymentController.class);

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    /**
     * Kullanıcının ödeme yöntemlerini listele
     * PROTECTED ENDPOINT - JWT token gerektirir
     * 
     * GET /api/payment/methods
     */
    @GetMapping("/methods")
    public ResponseEntity<List<PaymentMethodResponse>> getPaymentMethods(
            @RequestHeader("X-User-Id") String userId) {
        log.info("Received request to get payment methods for userId: {}", userId);
        
        List<PaymentMethodResponse> paymentMethods = paymentService.getPaymentMethods(userId);
        
        return ResponseEntity.ok(paymentMethods);
    }

    /**
     * Yeni ödeme yöntemi ekle
     * PROTECTED ENDPOINT - JWT token gerektirir
     * 
     * POST /api/payment/methods
     */
    @PostMapping("/methods")
    public ResponseEntity<PaymentMethodResponse> addPaymentMethod(
            @RequestHeader("X-User-Id") String userId,
            @Valid @RequestBody AddPaymentMethodRequest request) {
        log.info("Received request to add payment method for userId: {}", userId);
        
        PaymentMethodResponse paymentMethod = paymentService.addPaymentMethod(userId, request);
        
        return new ResponseEntity<>(paymentMethod, HttpStatus.CREATED);
    }

    /**
     * Ödeme yöntemini sil
     * PROTECTED ENDPOINT - JWT token gerektirir
     * 
     * DELETE /api/payment/methods/{id}
     */
    @DeleteMapping("/methods/{id}")
    public ResponseEntity<Void> deletePaymentMethod(
            @RequestHeader("X-User-Id") String userId,
            @PathVariable Long id) {
        log.info("Received request to delete payment method {} for userId: {}", id, userId);
        
        paymentService.deletePaymentMethod(userId, id);
        
        return ResponseEntity.noContent().build();
    }
}




