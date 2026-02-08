package com.microservices.content_management_service.exception;

/**
 * Bad Request Exception
 * Geçersiz istek durumunda fırlatılır
 */
public class BadRequestException extends RuntimeException {
    
    public BadRequestException(String message) {
        super(message);
    }
}






