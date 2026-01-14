package com.example.blogs.app.api.comment.exception;

public class FailedToFindCommentsByPostIdException extends RuntimeException {
    public FailedToFindCommentsByPostIdException(Exception e) {
        super("Failed to find comments by post ID", e);
    }
}
