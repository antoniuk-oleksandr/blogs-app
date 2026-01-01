package com.example.blogs.app.api.user.exception;

/**
 * Thrown when a database error occurs during user lookup operations.
 */
public class FailedToFindUserException extends RuntimeException {
    public FailedToFindUserException() {
        super("Failed to find user");
    }
}
