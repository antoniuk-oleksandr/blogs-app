package com.example.blogs.app.api.comment.exception;

/**
 * Exception thrown when comment retrieval by post ID fails due to a system error.
 */
public class FailedToFindCommentsByPostIdException extends RuntimeException {

    /**
     * Constructs a new FailedToFindCommentsByPostIdException with a default message and underlying cause.
     *
     * @param cause the underlying cause of the exception
     */
    public FailedToFindCommentsByPostIdException(Exception cause) {
        super("Failed to find comments by post ID", cause);
    }
}
