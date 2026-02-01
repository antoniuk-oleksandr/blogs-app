package com.example.blogs.app.api.file.service;

import com.example.blogs.app.api.file.entity.FileEntity;
import com.example.blogs.app.api.file.exception.FailedToUploadFileException;
import com.example.blogs.app.api.file.repository.adapter.FileRepositoryAdapter;
import com.example.blogs.app.storage.S3BucketService;
import com.example.blogs.app.util.FileNameParts;
import com.example.blogs.app.util.FileUtils;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.slf4j.MDC;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

/**
 * Orchestrates file upload operations by coordinating storage upload and metadata persistence.
 */
@Service
@AllArgsConstructor
public class FileServiceImpl implements FileService {

    private final FileRepositoryAdapter fileRepositoryAdapter;

    private final S3BucketService s3BucketService;

    private final FileUtils fileUtils;

    private static final Logger log = LoggerFactory.getLogger(FileServiceImpl.class);

    /**
     * Uploads a file to S3 storage and saves its metadata to the repository.
     * Generates a unique identifier, normalizes the path, detects content type, and uploads to S3.
     *
     * @param file     the multipart file to upload
     * @param filePath the storage path where the file should be uploaded
     * @return the saved file entity with metadata
     * @throws FailedToUploadFileException if the S3 upload fails
     */
    @Override
    @SneakyThrows
    public FileEntity upload(MultipartFile file, String filePath) {
        FileNameParts parts = fileUtils.extractFileNameParts(file.getOriginalFilename());

        String fileId = UUID.randomUUID().toString();
        String normalizedPath = fileUtils.normalizePath(filePath);
        String contentType = fileUtils.detectContentType(parts.extension());

        MDC.put("fileId", fileId);
        MDC.put("filePath", normalizedPath);
        MDC.put("fileExt", parts.extension());

        log.info(
                "Starting file upload: name={}, size={}, contentType={}",
                parts.name(),
                file.getSize(),
                contentType
        );

        try {
            s3BucketService.upload(
                    normalizedPath,
                    fileId,
                    parts.extension(),
                    contentType,
                    file.getBytes()
            );

            log.info("File successfully uploaded to S3");

            FileEntity saved =
                    fileRepositoryAdapter.save(
                            normalizedPath,
                            parts.name(),
                            parts.extension(),
                            fileId
                    );

            log.info(
                    "File metadata persisted: fileEntityId={}", saved.getId()
            );

            return saved;

        } catch (Exception e) {

            log.error(
                    "Failed to upload file to S3", e
            );

            throw new FailedToUploadFileException(e);

        } finally {
            MDC.clear();
        }
    }
}
