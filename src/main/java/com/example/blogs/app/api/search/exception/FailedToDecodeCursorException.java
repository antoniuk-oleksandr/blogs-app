package com.example.blogs.app.api.search.exception;

public class FailedToDecodeCursorException extends RuntimeException {

    public FailedToDecodeCursorException(Exception cause) {
        super("Failed to decode cursor", cause);
    }
}
