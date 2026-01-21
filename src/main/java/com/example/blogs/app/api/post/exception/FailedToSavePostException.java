package com.example.blogs.app.api.post.exception;

/**
 * Exception thrown when saving a post to the repository fails.
 */
public class FailedToSavePostException extends RuntimeException {
    /**
     * Constructs a new exception with the cause of the failure.
     *
     * @param cause the underlying cause that resulted in the save failure
     */
    public FailedToSavePostException(Throwable cause) {
        super("Failed to save post", cause);
    }
}
