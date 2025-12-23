package com.example.blogs.app.api.user.exception;

/**
 * Thrown when attempting to create a user with an already-existing email address.
 * Mapped to HTTP 409 CONFLICT.
 */
public class EmailTakenException extends RuntimeException {
    /**
     * Constructs a new EmailTakenException with a default error message.
     */
    public EmailTakenException() {
        super("Email is already taken");
    }
}
