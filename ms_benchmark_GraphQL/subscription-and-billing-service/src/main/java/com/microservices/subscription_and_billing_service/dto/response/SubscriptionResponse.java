package com.microservices.subscription_and_billing_service.dto.response;

import com.microservices.subscription_and_billing_service.entity.Subscription;
import com.microservices.subscription_and_billing_service.entity.SubscriptionPlan;
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

    /**
     * Convert Subscription entity to SubscriptionResponse DTO
     */
    public static SubscriptionResponse fromEntity(Subscription subscription) {
        if (subscription == null) {
            return null;
        }

        SubscriptionPlanResponse planResponse = null;
        if (subscription.getPlan() != null) {
            SubscriptionPlan plan = subscription.getPlan();
            planResponse = SubscriptionPlanResponse.builder()
                    .id(plan.getId())
                    .planName(plan.getPlanName())
                    .displayName(plan.getDisplayName())
                    .description(plan.getDescription())
                    .monthlyPrice(plan.getMonthlyPrice())
                    .yearlyPrice(plan.getYearlyPrice())
                    .maxScreens(plan.getMaxScreens())
                    .maxProfiles(plan.getMaxProfiles())
                    .videoQuality(plan.getVideoQuality())
                    .downloadAvailable(plan.getDownloadAvailable())
                    .adsIncluded(plan.getAdsIncluded())
                    .isActive(plan.getIsActive())
                    .build();
        }

        return SubscriptionResponse.builder()
                .id(subscription.getId())
                .userId(subscription.getUserId())
                .plan(planResponse)
                .status(subscription.getStatus() != null ? subscription.getStatus().name() : null)
                .billingCycle(subscription.getBillingCycle() != null ? subscription.getBillingCycle().name() : null)
                .startDate(subscription.getStartDate())
                .endDate(subscription.getEndDate())
                .cancelledAt(subscription.getCancelledAt())
                .cancellationReason(subscription.getCancellationReason())
                .autoRenew(subscription.getAutoRenew())
                .nextBillingDate(subscription.getNextBillingDate())
                .createdAt(subscription.getCreatedAt())
                .updatedAt(subscription.getUpdatedAt())
                .build();
    }
}
