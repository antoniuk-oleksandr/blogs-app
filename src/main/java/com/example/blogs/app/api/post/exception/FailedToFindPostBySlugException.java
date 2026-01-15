package com.example.blogs.app.api.post.exception;

public class FailedToFindPostBySlugException extends RuntimeException {

    public FailedToFindPostBySlugException(Exception e) {
        super("Failed to find post by slug", e);
    }
}
