package com.microservices.subscription_and_billing_service.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * UserProfile Response DTO (User Service'den alınan)
 * Bu DTO User Service'den gelen response'u temsil eder
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserProfileResponse {

    private Long id;
    private String userId;
    private String email;
    private String firstName;
    private String lastName;
    private Boolean isActive;
}




