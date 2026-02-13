package com.example.blogs.app.api.reaction.exception;

/**
 * Exception thrown when a requested reaction is not found.
 */
public class ReactionNotFoundException extends  RuntimeException {

    /**
     * Constructs a new exception with a default message and cause.
     *
     * @param cause the underlying exception that caused the failure
     */
    public ReactionNotFoundException(Exception cause) {
        super("Reaction not found", cause);
    }
}
