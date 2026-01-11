package com.example.blogs.app.api.auth.exception;

public class FailedToCheckTokenRevokedException extends RuntimeException {
    public FailedToCheckTokenRevokedException(Throwable cause) {
        super("Failed to check if token is revoked", cause);
    }
}
