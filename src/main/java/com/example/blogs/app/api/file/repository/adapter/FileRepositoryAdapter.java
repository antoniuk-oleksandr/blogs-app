package com.example.blogs.app.api.file.repository.adapter;

import com.example.blogs.app.api.file.entity.FileEntity;

/**
 * Adapter for file repository operations with exception handling.
 */
public interface FileRepositoryAdapter {
    /**
     * Saves file metadata to the repository.
     *
     * @param filePath  the storage path of the file
     * @param fileName  the original name of the file
     * @param extension the file extension including the dot (e.g., ".png")
     * @param uuid      the unique identifier for the file
     * @return the saved file entity with generated ID
     */
    FileEntity save(String filePath, String fileName, String extension, String uuid);

    void deleteById(Long fileId);
}
