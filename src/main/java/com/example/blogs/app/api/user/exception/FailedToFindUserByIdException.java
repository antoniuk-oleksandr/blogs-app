package com.example.blogs.app.api.user.exception;

public class FailedToFindUserByIdException extends RuntimeException {

    public FailedToFindUserByIdException(Exception cause) {
        super("Failed to find user by ID", cause);
    }
}
