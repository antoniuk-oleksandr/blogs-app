package com.example.blogs.app.api.auth.exception;

/**
 * Thrown when authentication credentials are invalid.
 */
public class InvalidUsernameEmailOrPasswordException extends RuntimeException {
    public InvalidUsernameEmailOrPasswordException() {
        super("Invalid username/email or password");
    }
}
