package com.microservices.subscription_and_billing_service.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Add Payment Method Request DTO
 * Yeni ödeme yöntemi ekleme isteği
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AddPaymentMethodRequest {

    @NotBlank(message = "Card holder name is required")
    @Size(max = 100, message = "Card holder name must be less than 100 characters")
    private String cardHolderName;

    @NotBlank(message = "Card number is required")
    @Pattern(regexp = "\\d{16}", message = "Card number must be 16 digits")
    private String cardNumber;

    @NotBlank(message = "Expiry month is required")
    @Pattern(regexp = "(0[1-9]|1[0-2])", message = "Expiry month must be between 01 and 12")
    private String expiryMonth;

    @NotBlank(message = "Expiry year is required")
    @Pattern(regexp = "\\d{4}", message = "Expiry year must be 4 digits")
    private String expiryYear;

    @NotBlank(message = "CVV is required")
    @Pattern(regexp = "\\d{3,4}", message = "CVV must be 3 or 4 digits")
    private String cvv;

    @NotBlank(message = "Card brand is required")
    private String cardBrand; // VISA, MASTERCARD, AMEX

    private Boolean setAsDefault = false;
}




