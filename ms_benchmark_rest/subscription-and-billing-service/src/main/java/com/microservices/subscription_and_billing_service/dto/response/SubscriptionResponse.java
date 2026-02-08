package com.microservices.subscription_and_billing_service.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Subscription Response DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SubscriptionResponse {

    private Long id;
    private String userId;
    private SubscriptionPlanResponse plan;
    private String status;
    private String billingCycle;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private LocalDateTime cancelledAt;
    private String cancellationReason;
    private Boolean autoRenew;
    private LocalDateTime nextBillingDate;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}




