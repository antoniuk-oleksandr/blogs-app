package com.example.blogs.app.api.post.exception;

public class FailedToUpdatePostException extends RuntimeException {

    public FailedToUpdatePostException(Exception e) {
        super("Failed to update post", e);
    }
}
