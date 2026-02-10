package com.example.blogs.app.api.comment.exception;

/**
 * Exception thrown when a requested comment is not found.
 */
public class CommentNotFoundException extends RuntimeException {

    /**
     * Constructs a new exception with a default message and cause.
     *
     * @param cause the underlying exception that caused the failure
     */
    public CommentNotFoundException(Exception cause) {
        super("Comment not found", cause);
    }
}
