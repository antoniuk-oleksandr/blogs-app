package com.example.blogs.app.api.file.exception;

/**
 * Exception thrown when saving a file entity to the repository fails.
 */
public class FailedToSaveFileException extends RuntimeException {

    /**
     * Constructs a new exception with the cause of the failure.
     *
     * @param e the underlying exception that caused the save failure
     */
    public FailedToSaveFileException(Exception e) {
        super("Failed to save file", e);
    }
}
