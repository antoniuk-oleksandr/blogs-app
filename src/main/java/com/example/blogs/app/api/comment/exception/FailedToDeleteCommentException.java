package com.example.blogs.app.api.comment.exception;

public class FailedToDeleteCommentException  extends  RuntimeException {

    public FailedToDeleteCommentException(Throwable cause) {
        super("Failed to delete comment", cause);
    }
}
