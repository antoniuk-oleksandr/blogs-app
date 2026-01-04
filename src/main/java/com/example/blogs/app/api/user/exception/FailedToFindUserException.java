package com.example.blogs.app.api.user.exception;

/**
 * Thrown when a database error occurs during user lookup operations.
 */
public class FailedToFindUserException extends RuntimeException {
    /**
     * Constructs a new FailedToFindUserException with a default message.
     */
    public FailedToFindUserException() {
        super("Failed to find user");
    }
}
