package com.example.blogs.app.api.search.exception;

public class FailedToSearchPostsException extends RuntimeException {

    public FailedToSearchPostsException(Exception cause) {
        super("Failed to search posts", cause);
    }
}
