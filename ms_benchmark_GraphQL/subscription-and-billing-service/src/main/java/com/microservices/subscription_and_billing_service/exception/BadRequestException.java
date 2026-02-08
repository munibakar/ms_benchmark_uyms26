package com.microservices.subscription_and_billing_service.exception;

/**
 * Bad Request Exception
 */
public class BadRequestException extends RuntimeException {
    public BadRequestException(String message) {
        super(message);
    }
}




