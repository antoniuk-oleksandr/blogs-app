package com.example.blogs.app.api.post.exception;

/**
 * Exception thrown when a post lookup by ID fails due to a repository error.
 */
public class FailedToFindPostByIdException extends RuntimeException {

    /**
     * Constructs a new exception wrapping the underlying repository error.
     *
     * @param cause the underlying cause of the exception
     */
    public FailedToFindPostByIdException(Exception cause) {
        super("Failed to find post by ID", cause);
    }
}
