package com.example.blogs.app.api.post.exception;

/**
 * Exception thrown when saving a post to the repository fails.
 */
public class FailedToSavePostException extends RuntimeException {
    /**
     * Constructs a new exception with the cause of the failure.
     *
     * @param cause the underlying cause of the exception
     */
    public FailedToSavePostException(Exception cause) {
        super("Failed to save post", cause);
    }
}
