package com.example.blogs.app.api.post.exception;

public class FailedToRollbackS3FileException extends RuntimeException {

    public FailedToRollbackS3FileException(Exception cause) {
        super("Failed to rollback S3 file upload", cause);
    }
}
