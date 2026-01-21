package com.example.blogs.app.storage.exception;

public class FailedToStoreFileException extends RuntimeException {

    public FailedToStoreFileException(Throwable cause) {
        super("Failed to store file ", cause);
    }
}
