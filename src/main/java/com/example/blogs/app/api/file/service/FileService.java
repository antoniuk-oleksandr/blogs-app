package com.example.blogs.app.api.file.service;

import com.example.blogs.app.api.file.entity.FileEntity;
import org.springframework.web.multipart.MultipartFile;

/**
 * Service for file upload operations and metadata management.
 */
public interface FileService {
    /**
     * Uploads a file to storage and saves its metadata.
     *
     * @param file     the multipart file to upload
     * @param filePath the storage path where the file should be uploaded
     * @return the saved file entity with metadata
     */
    FileEntity upload(MultipartFile file, String filePath);

    void delete(FileEntity fileEntity);
}
