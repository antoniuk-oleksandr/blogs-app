package com.example.blogs.app.api.post.exception;

/**
 * Exception thrown when a requested post does not exist.
 */
public class PostNotFoundException extends RuntimeException {

    /**
     * Constructs a new PostNotFound with a default message and underlying cause.
     *
     * @param e the exception that caused this post not found error
     */
    public PostNotFoundException(Exception e) {
        super("Post not found", e);
    }
}
