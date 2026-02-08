package com.microservice.user_service.exception;

/**
 * Bad Request Exception
 * Geçersiz istek durumunda fırlatılan hata
 */
public class BadRequestException extends RuntimeException {
    
    public BadRequestException(String message) {
        super(message);
    }
}

