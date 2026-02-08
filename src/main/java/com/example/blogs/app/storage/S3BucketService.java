package com.example.blogs.app.storage;

/**
 * Service for S3 bucket operations including file uploads.
 */
public interface S3BucketService {
    /**
     * Uploads a file to the S3 bucket with specified metadata.
     *
     * @param filePath    the storage path within the bucket
     * @param fileName    the file name without extension
     * @param extension   the file extension including the dot (e.g., ".png")
     * @param contentType the MIME type of the file
     * @param fileData    the file content as byte array
     */
    void upload(
            String filePath,
            String fileName,
            String extension,
            String contentType,
            byte[] fileData
    );

    /**
     * Deletes a file from the S3 bucket.
     *
     * @param filePath  the storage path within the bucket
     * @param fileName  the file name without extension
     * @param extension the file extension including the dot (e.g., ".png")
     */
    void delete(String filePath, String fileName, String extension);
}
