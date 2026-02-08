package com.microservices.subscription_and_billing_service.dto.response;

import com.microservices.subscription_and_billing_service.entity.BillingHistory;
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

    /**
     * Convert BillingHistory entity to BillingHistoryResponse DTO
     */
    public static BillingHistoryResponse fromEntity(BillingHistory billingHistory) {
        if (billingHistory == null) {
            return null;
        }

        return BillingHistoryResponse.builder()
                .id(billingHistory.getId())
                .userId(billingHistory.getUserId())
                .subscriptionId(billingHistory.getSubscriptionId())
                .planName(billingHistory.getPlanName())
                .amount(billingHistory.getAmount())
                .currency(billingHistory.getCurrency())
                .paymentStatus(
                        billingHistory.getPaymentStatus() != null ? billingHistory.getPaymentStatus().name() : null)
                .paymentMethod(
                        billingHistory.getPaymentMethod() != null ? billingHistory.getPaymentMethod().name() : null)
                .transactionId(billingHistory.getTransactionId())
                .paymentDate(billingHistory.getPaymentDate())
                .billingPeriodStart(billingHistory.getBillingPeriodStart())
                .billingPeriodEnd(billingHistory.getBillingPeriodEnd())
                .invoiceUrl(billingHistory.getInvoiceUrl())
                .failureReason(billingHistory.getFailureReason())
                .createdAt(billingHistory.getCreatedAt())
                .build();
    }
}
