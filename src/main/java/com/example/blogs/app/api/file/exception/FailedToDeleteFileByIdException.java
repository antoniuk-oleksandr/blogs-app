package com.example.blogs.app.api.file.exception;

/**
 * Thrown when deletion of file metadata by ID fails in the repository.
 */
public class FailedToDeleteFileByIdException extends RuntimeException {

    /**
     * Constructs a new exception with the specified cause.
     *
     * @param cause the underlying exception that caused the deletion failure
     */
    public FailedToDeleteFileByIdException(Exception cause) {
        super("Failed to delete file by ID", cause);
    }
}
