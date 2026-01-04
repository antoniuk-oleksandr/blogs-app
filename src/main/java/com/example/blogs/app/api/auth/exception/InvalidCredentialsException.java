package com.example.blogs.app.api.auth.exception;

/**
 * Thrown when authentication credentials are invalid.
 */
public class InvalidCredentialsException extends RuntimeException {
    /**
     * Constructs a new InvalidCredentialsException with a default message.
     */
    public InvalidCredentialsException() {
        super("Invalid username/email or password");
    }
}
