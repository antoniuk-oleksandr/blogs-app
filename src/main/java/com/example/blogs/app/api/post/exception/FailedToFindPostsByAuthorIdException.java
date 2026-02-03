package com.example.blogs.app.api.post.exception;

/**
 * Thrown when the repository fails to retrieve posts by author ID.
 */
public class FailedToFindPostsByAuthorIdException extends RuntimeException {
    /**
     * Constructs a new exception with the specified cause.
     *
     * @param cause the underlying cause of the exception
     */
    public FailedToFindPostsByAuthorIdException(Exception cause) {
        super("Failed to find posts by author ID", cause);
    }
}
