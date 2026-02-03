package com.example.blogs.app.api.user.exception;

/**
 * Thrown when a database error occurs during user lookup operations.
 */
public class FailedToFindUserException extends RuntimeException {
    /**
     * Constructs a new FailedToFindUserException with a default message.
     *
     * @param cause the underlying cause of the exception
     */
    public FailedToFindUserException(Exception cause) {
        super("Failed to find user", cause);
    }
}
