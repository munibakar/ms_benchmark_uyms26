package com.microservices.profile_service.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Subscription Response DTO
 * Subscription Service'ten gelen abonelik bilgisi
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SubscriptionResponse {
    
    private String id;
    private String userId;
    private SubscriptionPlanResponse plan;
    private String status;
    private String billingCycle;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private Boolean autoRenew;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SubscriptionPlanResponse {
        private String id;
        private String planName;
        private String displayName;
        private String description;
        private BigDecimal monthlyPrice;
        private BigDecimal yearlyPrice;
        private Integer maxScreens;
        private Integer maxProfiles;
        private String videoQuality;
        private Boolean downloadAvailable;
        private Boolean adsIncluded;
        private Boolean isActive;
    }
}
