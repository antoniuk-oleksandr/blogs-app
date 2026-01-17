package com.example.blogs.app.api.post.exception;

/**
 * Exception thrown when a post update operation fails due to a repository error.
 */
public class FailedToUpdatePostException extends RuntimeException {

    /**
     * Constructs a new exception wrapping the underlying repository error.
     *
     * @param e the underlying exception that caused the update to fail
     */
    public FailedToUpdatePostException(Exception e) {
        super("Failed to update post", e);
    }
}
