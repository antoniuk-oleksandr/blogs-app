package com.example.blogs.app.api.file.exception;

/**
 * Thrown when deletion of a file from S3 storage fails.
 */
public class FailedToDeleteFileException extends RuntimeException {

    /**
     * Constructs a new exception with the specified cause.
     *
     * @param cause the underlying exception that caused the deletion failure
     */
    public FailedToDeleteFileException(Exception cause) {
        super("Failed to delete file", cause);
    }
}
