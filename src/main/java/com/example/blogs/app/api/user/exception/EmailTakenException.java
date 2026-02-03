package com.example.blogs.app.api.user.exception;

/**
 * Thrown when attempting to create a user with an already-existing email address.
 * Mapped to HTTP 409 CONFLICT.
 */
public class EmailTakenException extends RuntimeException {
    /**
     * Constructs a new EmailTakenException with a default error message.
     *
     * @param cause the underlying cause of the exception
     */
    public EmailTakenException(Exception cause) {
        super("Email is already taken", cause);
    }
}
