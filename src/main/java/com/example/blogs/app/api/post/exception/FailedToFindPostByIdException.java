package com.example.blogs.app.api.post.exception;

/**
 * Exception thrown when a post lookup by ID fails due to a repository error.
 */
public class FailedToFindPostByIdException extends RuntimeException {

    /**
     * Constructs a new exception wrapping the underlying repository error.
     *
     * @param e the underlying exception that caused the lookup to fail
     */
    public FailedToFindPostByIdException(Throwable e) {
        super("Failed to find post by ID", e);
    }
}
