package com.example.blogs.app.api.auth.exception;

/**
 * Thrown when attempting to revoke a token that has already been revoked.
 * This exception is mapped to HTTP 409 Conflict status.
 */
public class TokenAlreadyRevokedException extends RuntimeException {
    /**
     * Constructs a new TokenAlreadyRevokedException with a default message.
     */
    public TokenAlreadyRevokedException() {
        super("Token is already revoked.");
    }
}
