package com.example.blogs.app.api.user.exception;

/**
 * Thrown when user profile update fails for reasons other than uniqueness constraint violations.
 * Mapped to HTTP 500 INTERNAL_SERVER_ERROR.
 */
public class FailedToUpdateUserException extends RuntimeException {

    /**
     * Constructs a new FailedToUpdateUser exception with a default error message.
     *
     * @param cause the underlying cause of the exception
     */
    public FailedToUpdateUserException(Exception cause) {
        super("Failed to update user", cause);
    }
}
