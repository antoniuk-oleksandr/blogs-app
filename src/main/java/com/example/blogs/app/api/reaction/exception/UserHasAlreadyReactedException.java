package com.example.blogs.app.api.reaction.exception;

/**
 * Exception thrown when a user attempts to create a duplicate reaction.
 */
public class UserHasAlreadyReactedException extends RuntimeException {

    /**
     * Constructs a new exception with a default message and cause.
     *
     * @param cause the underlying exception that caused the failure
     */
    public UserHasAlreadyReactedException(Exception cause) {
        super("User has already reacted", cause);
    }
}
