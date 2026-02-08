package com.microservices.subscription_and_billing_service.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * BillingHistory Response DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BillingHistoryResponse {

    private Long id;
    private String userId;
    private Long subscriptionId;
    private String planName;
    private BigDecimal amount;
    private String currency;
    private String paymentStatus;
    private String paymentMethod;
    private String transactionId;
    private LocalDateTime paymentDate;
    private LocalDateTime billingPeriodStart;
    private LocalDateTime billingPeriodEnd;
    private String invoiceUrl;
    private String failureReason;
    private LocalDateTime createdAt;
}




