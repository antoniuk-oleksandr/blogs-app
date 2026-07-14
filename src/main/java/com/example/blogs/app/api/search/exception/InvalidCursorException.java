package com.example.blogs.app.api.search.exception;

public class InvalidCursorException extends RuntimeException {

    public InvalidCursorException(Exception cause) {
        super("Invalid cursor provided", cause);
    }
}
