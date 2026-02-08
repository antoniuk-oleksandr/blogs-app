package com.example.blogs.app.api.file.exception;

public class FailedToDeleteFileException extends RuntimeException {

    public FailedToDeleteFileException(Exception cause) {
        super("Failed to delete file", cause);
    }
}
