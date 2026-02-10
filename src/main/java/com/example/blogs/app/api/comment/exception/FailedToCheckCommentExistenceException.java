package com.example.blogs.app.api.comment.exception;

/**
 * Exception thrown when a comment existence check operation fails.
 */
public class FailedToCheckCommentExistenceException extends RuntimeException {

    /**
     * Constructs a new exception with a default message and cause.
     *
     * @param cause the underlying exception that caused the failure
     */
    public FailedToCheckCommentExistenceException(Throwable cause) {
        super("Failed to check comment existence", cause);
    }
}
