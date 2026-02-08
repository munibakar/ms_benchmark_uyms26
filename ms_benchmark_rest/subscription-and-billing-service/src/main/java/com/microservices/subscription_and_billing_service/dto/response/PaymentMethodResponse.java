package com.microservices.subscription_and_billing_service.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * PaymentMethod Response DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentMethodResponse {

    private Long id;
    private String type;
    private String cardHolderName;
    private String lastFourDigits;
    private String cardBrand;
    private String expiryMonth;
    private String expiryYear;
    private Boolean isDefault;
    private Boolean isActive;
    private LocalDateTime createdAt;
}




