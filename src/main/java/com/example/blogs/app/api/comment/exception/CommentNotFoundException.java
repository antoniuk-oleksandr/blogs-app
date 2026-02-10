package com.example.blogs.app.api.comment.exception;

public class CommentNotFoundException extends RuntimeException {

    public CommentNotFoundException(Exception cause) {
        super("Comment not found", cause);
    }
}
