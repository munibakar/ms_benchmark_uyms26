package com.microservices.video_streaming_service.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Subscription Response DTO
 * Subscription Service'den dönen abonelik bilgisi
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SubscriptionResponse {

    private Long id;
    private String userId;
    private String status;
    private String billingCycle;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private Boolean autoRenew;
}
