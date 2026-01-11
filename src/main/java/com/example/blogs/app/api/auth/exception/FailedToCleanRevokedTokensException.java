package com.example.blogs.app.api.auth.exception;

public class FailedToCleanRevokedTokensException extends RuntimeException {
    public FailedToCleanRevokedTokensException(Throwable cause) {
        super("Failed to clean expired revoked tokens", cause);
    }
}
