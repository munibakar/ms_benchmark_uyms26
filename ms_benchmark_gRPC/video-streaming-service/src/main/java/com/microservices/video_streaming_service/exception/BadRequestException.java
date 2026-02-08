package com.microservices.video_streaming_service.exception;

/**
 * Bad Request Exception
 * Geçersiz istek durumunda fırlatılır
 */
public class BadRequestException extends RuntimeException {
    
    public BadRequestException(String message) {
        super(message);
    }
}






