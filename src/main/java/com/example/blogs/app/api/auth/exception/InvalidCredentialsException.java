package com.example.blogs.app.api.auth.exception;

/**
 * Thrown when authentication credentials are invalid.
 */
public class InvalidCredentialsException extends RuntimeException {
    /**
     * Constructs a new InvalidCredentialsException with a default message.
     *
     * @param cause the underlying cause of the exception
     */
    public InvalidCredentialsException(Exception cause) {
        super("Invalid username/email or password", cause);
    }
}
