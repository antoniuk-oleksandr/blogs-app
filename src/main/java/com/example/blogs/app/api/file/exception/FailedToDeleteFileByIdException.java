package com.example.blogs.app.api.file.exception;

public class FailedToDeleteFileByIdException extends RuntimeException {

    public FailedToDeleteFileByIdException(Exception cause) {
        super("Failed to delete file by ID", cause);
    }
}
