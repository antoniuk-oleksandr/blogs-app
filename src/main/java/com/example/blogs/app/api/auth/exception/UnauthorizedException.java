package com.example.blogs.app.api.auth.exception;

/**
 * Thrown when authentication credentials are invalid.
 */
public class UnauthorizedException extends RuntimeException {
    public UnauthorizedException() {
        super("Invalid username/email or password");
    }
}
