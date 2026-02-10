package com.example.blogs.app.api.comment.exception;

/**
 * Exception thrown when a comment update operation fails.
 */
public class FailedToUpdateCommentException extends RuntimeException {

    /**
     * Constructs a new exception with a default message and cause.
     *
     * @param cause the underlying exception that caused the failure
     */
    public FailedToUpdateCommentException(Exception cause) {
        super("Failed to update comment", cause);
    }
}
