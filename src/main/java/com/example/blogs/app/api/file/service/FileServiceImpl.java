package com.example.blogs.app.api.file.service;

import com.example.blogs.app.api.file.entity.FileEntity;
import com.example.blogs.app.api.file.exception.FailedToUploadFileException;
import com.example.blogs.app.api.file.repository.adapter.FileRepositoryAdapter;
import com.example.blogs.app.logging.MDCKeys;
import com.example.blogs.app.storage.S3BucketService;
import com.example.blogs.app.util.FileNameParts;
import com.example.blogs.app.util.FileUtils;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

        try {
            s3BucketService.upload(
                    normalizedPath,
                    fileId,
                    parts.extension(),
                    contentType,
                    file.getBytes()
            );

            FileEntity saved = fileRepositoryAdapter.save(
                    normalizedPath,
                    parts.name(),
                    parts.extension(),
                    fileId
            );

            log.info("file_uploaded fileId={} fileName={} size={} userId={} requestId={}",
                    saved.getId(), parts.name(), file.getSize(),
                    MDC.get(MDCKeys.USER_ID), MDC.get(MDCKeys.REQUEST_ID));

            return saved;

        } catch (Exception e) { 
            log.error("file_upload_failed fileName={} fileId={} filePath={} error={} requestId={}",
                    parts.name(), fileId, normalizedPath, e.getMessage(), MDC.get(MDCKeys.REQUEST_ID), e);
            throw new FailedToUploadFileException(e);
        }
    }

    /**
     * Deletes a file from S3 storage and removes its metadata from the repository.
     * Removes the file from S3 bucket first, then deletes the metadata record.
     *
     * @param fileEntity the file entity to delete
     */
    @Override
    public void delete(FileEntity fileEntity) {
        s3BucketService.delete(
                fileEntity.getFilePath(),
                fileEntity.getUuid(),
                fileEntity.getFileExtension()
        );
        fileRepositoryAdapter.deleteById(fileEntity.getId());
        log.info("file_deleted fileId={} userId={} requestId={}",
                fileEntity.getId(), MDC.get(MDCKeys.USER_ID), MDC.get(MDCKeys.REQUEST_ID));
    }
}
