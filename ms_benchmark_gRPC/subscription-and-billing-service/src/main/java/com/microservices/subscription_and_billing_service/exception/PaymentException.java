package com.microservices.subscription_and_billing_service.exception;

/**
 * Payment Exception
 * Ödeme işlemlerinde oluşan hatalar için
 */
public class PaymentException extends RuntimeException {
    public PaymentException(String message) {
        super(message);
    }
    
    public PaymentException(String message, Throwable cause) {
        super(message, cause);
    }
}




