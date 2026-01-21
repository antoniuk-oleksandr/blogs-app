package com.example.blogs.app.storage;

/**
 * Interface for building file access links from storage metadata.
 */
public interface FileLinkBuilder {
    /**
     * Builds a complete URL for accessing a file.
     *
     * @param filePath      the storage path of the file
     * @param uuid          the unique identifier of the file
     * @param fileExtension the file extension including the dot (e.g., ".png")
     * @return the complete file access URL
     */
    String buildLink(String filePath, String uuid, String fileExtension);
}
