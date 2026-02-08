package com.microservices.subscription_and_billing_service.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * SubscriptionPlan Response DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SubscriptionPlanResponse {

    private Long id;
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




