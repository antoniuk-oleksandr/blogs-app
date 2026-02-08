package com.example.blogs.app.api.post.exception;

/**
 * Thrown when automatic rollback cleanup of an S3 file upload fails during transaction rollback.
 */
public class FailedToRollbackS3FileException extends RuntimeException {

    /**
     * Constructs a new exception with the specified cause.
     *
     * @param cause the underlying exception that caused the rollback failure
     */
    public FailedToRollbackS3FileException(Exception cause) {
        super("Failed to rollback S3 file upload", cause);
    }
}
