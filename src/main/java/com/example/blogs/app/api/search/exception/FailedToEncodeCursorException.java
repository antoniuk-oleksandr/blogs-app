package com.example.blogs.app.api.search.exception;

public class FailedToEncodeCursorException extends RuntimeException {

    public FailedToEncodeCursorException(Exception cause) {
        super("Failed to encode cursor", cause);
    }
}
