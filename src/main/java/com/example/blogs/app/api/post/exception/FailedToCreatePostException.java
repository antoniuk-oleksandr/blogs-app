package com.example.blogs.app.api.post.exception;

/**
 * Exception thrown when post creation fails due to underlying service or repository errors.
 */
public class FailedToCreatePostException extends RuntimeException {

    /**
     * Constructs a new exception with the cause of the failure.
     *
     * @param cause the underlying cause of the exception
     */
    public FailedToCreatePostException(Exception cause) {
        super("Failed to create post", cause);
    }
}
