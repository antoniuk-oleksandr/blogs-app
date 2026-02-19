package com.example.blogs.app.api.user.exception;

public class FailedToUpdateUserException extends RuntimeException {

    public FailedToUpdateUserException(Exception cause) {
        super("Failed to update user", cause);
    }
}
