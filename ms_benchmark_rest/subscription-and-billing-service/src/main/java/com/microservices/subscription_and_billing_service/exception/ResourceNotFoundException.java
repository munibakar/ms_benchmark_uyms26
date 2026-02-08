package com.microservices.subscription_and_billing_service.exception;

/**
 * Resource Not Found Exception
 */
public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {
        super(message);
    }
}




