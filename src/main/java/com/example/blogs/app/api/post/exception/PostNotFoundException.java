package com.example.blogs.app.api.post.exception;

/**
 * Exception thrown when a requested post does not exist.
 */
public class PostNotFoundException extends RuntimeException {

    /**
     * Constructs a new PostNotFound with a default message and underlying cause.
     *
     * @param cause the underlying cause of the exception
     */
    public PostNotFoundException(Exception cause) {
        super("Post not found", cause);
    }
}
