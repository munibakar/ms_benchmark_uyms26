package com.authentication.microservices.authentication.exception;

/**
 * Resource Not Found Exception
 */
public class ResourceNotFoundException extends RuntimeException {
    
    public ResourceNotFoundException(String message) {
        super(message);
    }
}

