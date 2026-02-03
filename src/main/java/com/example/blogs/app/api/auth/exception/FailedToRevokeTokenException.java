package com.example.blogs.app.api.auth.exception;

/**
 * Thrown when a database error occurs while attempting to persist a revoked token.
 * This exception is mapped to HTTP 500 Internal Server Error status.
 */
public class FailedToRevokeTokenException extends RuntimeException {
    /**
     * Constructs a new FailedToRevokeTokenException with a default message.
     *
     * @param cause the underlying cause of the exception
     */
    public FailedToRevokeTokenException(Exception cause) {
        super("Failed to revoke token", cause);
    }
}
