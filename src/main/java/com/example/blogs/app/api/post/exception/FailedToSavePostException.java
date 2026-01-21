package com.example.blogs.app.api.post.exception;

public class FailedToSavePostException extends RuntimeException {
    public FailedToSavePostException(Throwable cause) {
        super("Failed to save post", cause);
    }
}
