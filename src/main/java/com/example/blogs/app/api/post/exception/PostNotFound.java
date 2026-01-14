package com.example.blogs.app.api.post.exception;

public class PostNotFound extends RuntimeException {

    public PostNotFound(Exception e) {
        super("Post not found", e);
    }
}
