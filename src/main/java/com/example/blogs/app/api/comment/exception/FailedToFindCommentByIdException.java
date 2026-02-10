package com.example.blogs.app.api.comment.exception;

/**
 * Exception thrown when a comment lookup by ID operation fails.
 */
public class FailedToFindCommentByIdException extends RuntimeException {

    /**
     * Constructs a new exception with a default message and cause.
     *
     * @param cause the underlying exception that caused the failure
     */
    public FailedToFindCommentByIdException( Exception cause) {
        super("Failed to find comment by ID", cause);
    }
}
