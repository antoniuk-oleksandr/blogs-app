package com.example.blogs.app.api.post.exception;

/**
 * Exception thrown when post retrieval by slug fails due to a system error.
 */
public class FailedToFindPostBySlugException extends RuntimeException {

    /**
     * Constructs a new FailedToFindPostBySlugException with a default message and underlying cause.
     *
     * @param e the exception that caused this post retrieval failure
     */
    public FailedToFindPostBySlugException(Exception e) {
        super("Failed to find post by slug", e);
    }
}
