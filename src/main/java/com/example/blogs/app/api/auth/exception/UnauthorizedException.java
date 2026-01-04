package com.example.blogs.app.api.auth.exception;

/**
 * Thrown when a request fails authorization due to invalid, expired, or missing authentication credentials.
 * This exception is mapped to HTTP 401 Unauthorized status.
 */
public class UnauthorizedException extends RuntimeException {
    /**
     * Constructs a new UnauthorizedException with a default message.
     */
    public UnauthorizedException() {
        super("Unauthorized access");
    }
}
