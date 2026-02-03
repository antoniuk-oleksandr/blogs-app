package com.example.blogs.app.api.post.exception;

/**
 * Exception thrown when a post update operation fails due to a repository error.
 */
public class FailedToUpdatePostException extends RuntimeException {

    /**
     * Constructs a new exception wrapping the underlying repository error.
     *
     * @param cause the underlying cause of the exception
     */
    public FailedToUpdatePostException(Exception cause) {
        super("Failed to update post", cause);
    }
}
