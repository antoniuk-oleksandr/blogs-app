package com.example.blogs.app.api.comment.exception;

public class FailedToCreateCommentException extends RuntimeException {

    public FailedToCreateCommentException(Exception cause) {
        super("Failed to create comment", cause);
    }
}
