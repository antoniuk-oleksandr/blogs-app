package com.example.blogs.app.api.file.exception;

/**
 * Exception thrown when uploading a file to storage fails.
 */
public class FailedToUploadFileException extends RuntimeException {

    /**
     * Constructs a new exception with the cause of the failure.
     *
     * @param e the underlying exception that caused the upload failure
     */
    public FailedToUploadFileException(Exception e) {
        super("Failed to upload file", e);
    }
}
