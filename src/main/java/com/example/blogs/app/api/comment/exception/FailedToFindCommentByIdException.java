package com.example.blogs.app.api.comment.exception;

public class FailedToFindCommentByIdException extends RuntimeException {

    public FailedToFindCommentByIdException( Exception cause) {
        super("Failed to find comment by ID", cause);
    }
}
