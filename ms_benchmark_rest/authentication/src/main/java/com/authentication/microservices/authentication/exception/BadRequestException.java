package com.authentication.microservices.authentication.exception;

/**
 * Bad Request Exception
 */
public class BadRequestException extends RuntimeException {
    
    public BadRequestException(String message) {
        super(message);
    }
}

