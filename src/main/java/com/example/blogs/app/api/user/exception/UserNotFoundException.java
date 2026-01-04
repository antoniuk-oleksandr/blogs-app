package com.example.blogs.app.api.user.exception;

/**
 * Thrown when a requested user does not exist in the system.
 */
public class UserNotFoundException extends RuntimeException {
    /**
     * Constructs a new UserNotFoundException with a default message.
     */
    public UserNotFoundException() {
        super("User not found");
    }
}
