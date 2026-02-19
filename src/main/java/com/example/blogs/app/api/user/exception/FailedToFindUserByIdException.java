package com.example.blogs.app.api.user.exception;

/**
 * Thrown when user lookup by ID fails for database-related reasons.
 * Mapped to HTTP 500 INTERNAL_SERVER_ERROR.
 */
public class FailedToFindUserByIdException extends RuntimeException {

    /**
     * Constructs a new FailedToFindUserById exception with a default error message.
     *
     * @param cause the underlying cause of the exception
     */
    public FailedToFindUserByIdException(Exception cause) {
        super("Failed to find user by ID", cause);
    }
}
