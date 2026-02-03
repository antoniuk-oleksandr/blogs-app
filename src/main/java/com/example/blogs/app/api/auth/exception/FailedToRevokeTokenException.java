package com.example.blogs.app.api.auth.exception;

/**
 * Thrown when a database error occurs while attempting to persist a revoked token.
 * This exception is mapped to HTTP 500 Internal Server Error status.
 */
public class FailedToRevokeTokenExecption extends RuntimeException {
    /**
     * Constructs a new FailedToRevokeTokenExecption with a default message.
     */
    public FailedToRevokeTokenExecption() {
        super("Failed to revoke token");
    }
}
