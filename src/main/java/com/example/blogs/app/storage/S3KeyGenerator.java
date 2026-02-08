package com.example.blogs.app.storage;

import org.springframework.stereotype.Component;

/**
 * Generates S3 object keys by combining file path, name, and extension.
 */
@Component
public class S3KeyGenerator {

    /**
     * Generates an S3 object key from file components.
     * Ensures proper path separator between path and filename.
     *
     * @param filePath  the storage path within the bucket
     * @param fileName  the file name without extension
     * @param extension the file extension including the dot (e.g., ".png")
     * @return the complete S3 object key
     */
    public String generateKey(String filePath, String fileName, String extension) {
       return  (filePath.endsWith("/") ? filePath + fileName : filePath + "/" + fileName) + extension;
    }
}
