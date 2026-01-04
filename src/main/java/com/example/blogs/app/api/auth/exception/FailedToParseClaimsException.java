package com.example.blogs.app.api.auth.exception;

/**
 * Thrown when JWT token claims cannot be parsed due to invalid format or signature.
 * This typically indicates a malformed or tampered token.
 */
public class FailedToParseClaimsException extends RuntimeException {
    /**
     * Constructs a new FailedToParseClaims exception with a default message.
     */
    public FailedToParseClaimsException() {
        super("Failed to parse claims from JWT token");
    }
}
