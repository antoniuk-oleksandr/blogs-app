package com.example.blogs.app.api.user.exception;

/**
 * Thrown when attempting to create a user with an already-existing username.
 * Mapped to HTTP 409 CONFLICT.
 */
public class UsernameTakenException extends RuntimeException {
    /**
     * Constructs a new UsernameTakenException with a default error message.
     *
     * @param cause the underlying cause of the exception
     */
    public UsernameTakenException(Exception cause) {
        super("Username is already taken", cause);
    }
}
