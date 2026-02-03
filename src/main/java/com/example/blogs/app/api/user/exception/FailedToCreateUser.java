package com.example.blogs.app.api.user.exception;

/**
 * Thrown when user creation fails for reasons other than uniqueness constraint violations.
 * Mapped to HTTP 500 INTERNAL_SERVER_ERROR.
 */
public class FailedToCreateUser extends RuntimeException {
    /**
     * Constructs a new FailedToCreateUser exception with a default error message.
     *
     * @param cause the underlying cause of the exception
     */
    public FailedToCreateUser(Exception cause) {
        super("Failed to create user", cause);
    }
}
