package com.microservices.subscription_and_billing_service.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Subscribe Request DTO
 * Yeni abonelik oluşturma isteği
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SubscribeRequest {

    @NotBlank(message = "Plan name is required")
    private String planName; // BASIC, STANDARD, PREMIUM

    @NotBlank(message = "Billing cycle is required")
    private String billingCycle; // MONTHLY, YEARLY

    private Long paymentMethodId; // Kullanılacak ödeme yöntemi ID'si (opsiyonel, yoksa default)
}




