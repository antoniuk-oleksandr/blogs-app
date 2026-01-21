package com.example.blogs.app.api.post.exception;

public class FailedToCreatePostException extends RuntimeException {

    public FailedToCreatePostException(Exception e) {
        super("Failed to create post", e);
    }
}
