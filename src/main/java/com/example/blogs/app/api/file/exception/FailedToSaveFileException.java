package com.example.blogs.app.api.file.exception;

/**
 * Exception thrown when saving a file entity to the repository fails.
 */
public class FailedToSaveFileException extends RuntimeException {

    /**
     * Constructs a new exception with the cause of the failure.
     *
     * @param cause the underlying cause of the exception
     */
    public FailedToSaveFileException(Exception cause) {
        super("Failed to save file", cause);
    }
}
