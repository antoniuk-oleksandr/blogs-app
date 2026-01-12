package com.example.blogs.app.api.post.exception;

/**
 * Thrown when the repository fails to retrieve posts by author ID.
 */
public class FailedToFindPostsByAuthorIdException extends RuntimeException {
    /**
     * Constructs a new exception with the specified cause.
     *
     * @param e the underlying exception that caused the failure
     */
    public FailedToFindPostsByAuthorIdException(Exception e) {
        super("Failed to find posts by author ID", e);
    }
}
