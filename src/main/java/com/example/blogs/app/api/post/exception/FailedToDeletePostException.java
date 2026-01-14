package com.example.blogs.app.api.post.exception;

public class FailedToDeletePostException extends RuntimeException {
    public FailedToDeletePostException(Exception e) {
        super("Failed to delete post", e);
    }
}
