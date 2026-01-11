package com.example.blogs.app.api.auth.exception;

/**
 * Thrown when a database error occurs while checking if a token has been revoked.
 * This exception is mapped to HTTP 500 Internal Server Error status.
 */
public class FailedToCheckTokenRevokedException extends RuntimeException {
    /**
     * Constructs a new FailedToCheckTokenRevokedException with the underlying cause.
     *
     * @param cause the database exception that prevented the revocation check
     */
    public FailedToCheckTokenRevokedException(Throwable cause) {
        super("Failed to check if token is revoked", cause);
    }
}
