package com.example.blogs.app.api.reaction.exception;

/**
 * Exception thrown when a reaction lookup operation fails.
 */
public class FailedToFindReactionException extends RuntimeException {

    /**
     * Constructs a new exception with a default message and cause.
     *
     * @param cause the underlying exception that caused the failure
     */
    public FailedToFindReactionException(Exception cause) {
        super("Failed to find reaction", cause);
    }
}
