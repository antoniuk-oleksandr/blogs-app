package com.example.blogs.app.api.file.exception;

public class FailedToSaveFileException extends RuntimeException {

    public FailedToSaveFileException(Exception e) {
        super("Failed to save file", e);
    }
}
