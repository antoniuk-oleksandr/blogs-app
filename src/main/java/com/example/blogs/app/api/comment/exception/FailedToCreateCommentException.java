package com.example.blogs.app.api.comment.exception;

/**
 * Exception thrown when a comment creation operation fails.
 */
public class FailedToCreateCommentException extends RuntimeException {

    /**
     * Constructs a new exception with a default message and cause.
     *
     * @param cause the underlying exception that caused the failure
     */
    public FailedToCreateCommentException(Exception cause) {
        super("Failed to create comment", cause);
    }
}
