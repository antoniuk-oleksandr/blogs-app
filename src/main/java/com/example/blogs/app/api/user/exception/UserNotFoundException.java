package com.example.blogs.app.api.user.exception;

/**
 * Thrown when a requested user does not exist in the system.
 */
public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException() {
        super("User not found");
    }
}
