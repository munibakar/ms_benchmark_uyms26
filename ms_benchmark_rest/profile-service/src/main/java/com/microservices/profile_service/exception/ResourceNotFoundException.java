package com.microservices.profile_service.exception;

/**
 * Resource Not Found Exception
 * Kaynak bulunamadığında fırlatılan hata
 */
public class ResourceNotFoundException extends RuntimeException {
    
    public ResourceNotFoundException(String message) {
        super(message);
    }
}
