package com.microservices.video_streaming_service.exception;

/**
 * Exception thrown when user tries to stream content without an active subscription
 */
public class SubscriptionRequiredException extends RuntimeException {

    public SubscriptionRequiredException(String message) {
        super(message);
    }

    public SubscriptionRequiredException(String message, Throwable cause) {
        super(message, cause);
    }
}
