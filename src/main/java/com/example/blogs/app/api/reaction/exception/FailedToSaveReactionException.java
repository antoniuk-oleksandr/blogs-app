package com.example.blogs.app.api.reaction.exception;

/**
 * Exception thrown when a reaction save operation fails.
 */
public class FailedToSaveReactionException extends RuntimeException{

    /**
     * Constructs a new exception with a default message and cause.
     *
     * @param cause the underlying exception that caused the failure
     */
    public FailedToSaveReactionException(Exception cause) {
        super("Failed to save reaction", cause);
    }
}
