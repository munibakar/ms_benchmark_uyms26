package com.microservices.content_management_service.exception;

/**
 * Resource Not Found Exception
 * Kaynak bulunamadığında fırlatılır
 */
public class ResourceNotFoundException extends RuntimeException {
    
    public ResourceNotFoundException(String message) {
        super(message);
    }
}






