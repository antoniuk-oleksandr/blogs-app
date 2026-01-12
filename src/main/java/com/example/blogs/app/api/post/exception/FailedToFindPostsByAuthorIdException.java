package com.example.blogs.app.api.post.exception;

public class FailedToFindPostsByAuthorIdException extends RuntimeException {
    public FailedToFindPostsByAuthorIdException(Exception e) {
        super("Failed to find posts by author ID", e);
    }
}
