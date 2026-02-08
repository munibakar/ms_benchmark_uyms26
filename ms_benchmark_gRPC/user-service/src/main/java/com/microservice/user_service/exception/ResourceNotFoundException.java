package com.microservice.user_service.exception;

/**
 * Resource Not Found Exception
 * Kaynak bulunamadığında fırlatılan hata
 */
public class ResourceNotFoundException extends RuntimeException {
    
    public ResourceNotFoundException(String message) {
        super(message);
    }
}

