package com.example.blogs.app.api.file.exception;

public class FailedToUploadFileException extends RuntimeException {

    public FailedToUploadFileException(Exception e) {
        super("Failed to upload file", e);
    }
}
