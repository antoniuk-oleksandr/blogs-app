package com.example.blogs.app.api.comment.exception;

/**
 * Exception thrown when a comment deletion operation fails.
 */
public class FailedToDeleteCommentException extends RuntimeException {

    /**
     * Constructs a new exception with a default message and cause.
     *
     * @param cause the underlying exception that caused the failure
     */
    public FailedToDeleteCommentException(Throwable cause) {
        super("Failed to delete comment", cause);
    }
}
