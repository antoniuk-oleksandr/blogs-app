package com.example.blogs.app.storage.exception;

/**
 * Exception thrown when storing a file to S3 storage fails.
 */
public class FailedToStoreFileException extends RuntimeException {

    /**
     * Constructs a new exception with the cause of the failure.
     *
     * @param cause the underlying cause that resulted in the storage failure
     */
    public FailedToStoreFileException(Throwable cause) {
        super("Failed to store file ", cause);
    }
}
