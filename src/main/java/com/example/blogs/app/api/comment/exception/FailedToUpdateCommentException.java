package com.example.blogs.app.api.comment.exception;

public class FailedToUpdateCommentException extends RuntimeException {

    public FailedToUpdateCommentException(Exception cause) {
        super("Failed to update comment", cause);
    }
}
