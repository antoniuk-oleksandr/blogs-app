package com.example.blogs.app.api.comment.exception;

public class FailedToCheckCommentExistenceException extends RuntimeException {

    public FailedToCheckCommentExistenceException(Throwable cause) {
        super("Failed to check comment existence", cause);
    }
}
