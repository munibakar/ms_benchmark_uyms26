package com.microservices.subscription_and_billing_service.service;

import com.microservices.subscription_and_billing_service.dto.response.BillingHistoryResponse;
import com.microservices.subscription_and_billing_service.entity.BillingHistory;
import com.microservices.subscription_and_billing_service.repository.BillingHistoryRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Billing Service
 * Fatura ve ödeme geçmişi yönetimi
 */
@Service
public class BillingService {

    private static final Logger log = LoggerFactory.getLogger(BillingService.class);

    private final BillingHistoryRepository billingHistoryRepository;

    public BillingService(BillingHistoryRepository billingHistoryRepository) {
        this.billingHistoryRepository = billingHistoryRepository;
    }

    /**
     * Kullanıcının fatura geçmişini getir
     */
    @Transactional(readOnly = true)
    public List<BillingHistoryResponse> getBillingHistory(String userId) {
        log.info("Fetching billing history for userId: {}", userId);
        
        List<BillingHistory> billingHistory = billingHistoryRepository.findByUserIdOrderByCreatedAtDesc(userId);
        
        return billingHistory.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Başarılı ödemeleri getir
     */
    @Transactional(readOnly = true)
    public List<BillingHistoryResponse> getSuccessfulPayments(String userId) {
        log.info("Fetching successful payments for userId: {}", userId);
        
        List<BillingHistory> payments = billingHistoryRepository.findByUserIdAndPaymentStatusOrderByCreatedAtDesc(
                userId, BillingHistory.PaymentStatus.SUCCESS);
        
        return payments.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Entity'yi Response DTO'ya çevir
     */
    private BillingHistoryResponse convertToResponse(BillingHistory billing) {
        return BillingHistoryResponse.builder()
                .id(billing.getId())
                .userId(billing.getUserId())
                .subscriptionId(billing.getSubscriptionId())
                .planName(billing.getPlanName())
                .amount(billing.getAmount())
                .currency(billing.getCurrency())
                .paymentStatus(billing.getPaymentStatus().name())
                .paymentMethod(billing.getPaymentMethod().name())
                .transactionId(billing.getTransactionId())
                .paymentDate(billing.getPaymentDate())
                .billingPeriodStart(billing.getBillingPeriodStart())
                .billingPeriodEnd(billing.getBillingPeriodEnd())
                .invoiceUrl(billing.getInvoiceUrl())
                .failureReason(billing.getFailureReason())
                .createdAt(billing.getCreatedAt())
                .build();
    }
}




