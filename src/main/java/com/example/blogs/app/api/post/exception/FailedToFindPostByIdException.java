package com.example.blogs.app.api.post.exception;

public class FailedToFindPostByIdException extends RuntimeException {

    public FailedToFindPostByIdException(Throwable e) {
        super("Failed to find post by ID", e);
    }
}
