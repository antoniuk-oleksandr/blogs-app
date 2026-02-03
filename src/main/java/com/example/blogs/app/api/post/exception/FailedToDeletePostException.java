package com.example.blogs.app.api.post.exception;

/**
 * Exception thrown when a post deletion operation fails due to database or system errors.
 */
public class FailedToDeletePostException extends RuntimeException {
    /**
     * Constructs a new FailedToDeletePostException with a default message and underlying cause.
     *
     * @param cause the underlying cause of the exception
     */
    public FailedToDeletePostException(Exception cause) {
        super("Failed to delete post", cause);
    }
}
