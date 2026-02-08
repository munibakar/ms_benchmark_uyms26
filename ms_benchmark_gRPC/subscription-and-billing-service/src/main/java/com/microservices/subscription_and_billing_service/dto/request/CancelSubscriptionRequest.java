package com.microservices.subscription_and_billing_service.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Cancel Subscription Request DTO
 * Abonelik iptal etme isteği
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CancelSubscriptionRequest {

    private String reason; // İptal nedeni (opsiyonel)
    
    private Boolean immediate = false; // Hemen iptal et mi, yoksa dönem sonunda mı
}




