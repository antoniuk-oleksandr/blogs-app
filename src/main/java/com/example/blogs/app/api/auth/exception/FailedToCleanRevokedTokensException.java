package com.example.blogs.app.api.auth.exception;

/**
 * Thrown when a database error occurs during scheduled cleanup of expired revoked tokens.
 * This exception is mapped to HTTP 500 Internal Server Error status.
 */
public class FailedToCleanRevokedTokensException extends RuntimeException {
    /**
     * Constructs a new FailedToCleanRevokedTokensException with the underlying cause.
     *
     * @param cause the database exception that prevented cleanup
     */
    public FailedToCleanRevokedTokensException(Throwable cause) {
        super("Failed to clean expired revoked tokens", cause);
    }
}
