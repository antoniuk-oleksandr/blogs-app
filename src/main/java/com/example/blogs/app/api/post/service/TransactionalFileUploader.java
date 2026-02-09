package com.example.blogs.app.api.post.service;

import com.example.blogs.app.api.file.entity.FileEntity;
import com.example.blogs.app.api.file.service.FileService;
import com.example.blogs.app.api.post.exception.FailedToRollbackS3FileException;
import com.example.blogs.app.logging.MDCKeys;
import com.example.blogs.app.storage.S3BucketService;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.web.multipart.MultipartFile;

/**
 * Handles file uploads with automatic rollback when database transactions fail.
 */
@Service
@AllArgsConstructor
public class TransactionalFileUploader {

    private static final Logger log = LoggerFactory.getLogger(TransactionalFileUploader.class);

    private final FileService fileService;

    private final S3BucketService s3BucketService;

    /**
     * Uploads a file and registers a transaction synchronization callback to delete it if the transaction rolls back.
     * Ensures uploaded files are cleaned up from S3 when the associated database transaction fails.
     *
     * @param file the multipart file to upload
     * @param path the storage path where the file should be uploaded
     * @return the uploaded file entity with metadata
     * @throws FailedToRollbackS3FileException if the rollback cleanup fails
     */
    public FileEntity uploadWithTransactionRollback(MultipartFile file, String path) {
        FileEntity uploadedFile = fileService.upload(file, path);

        TransactionSynchronizationManager.registerSynchronization(
                new TransactionSynchronization() {
                    @Override
                    public void afterCompletion(int status) {
                        if (status != STATUS_COMMITTED) {
                            try {
                                fileService.delete(uploadedFile);
                                s3BucketService.delete(
                                        uploadedFile.getFilePath(),
                                        uploadedFile.getUuid(),
                                        uploadedFile.getFileExtension()
                                );
                            } catch (Exception e) { 
                                log.error("s3_rollback_failed fileId={} filePath={} error={} requestId={}",
                                        uploadedFile.getId(), uploadedFile.getFilePath(),
                                        e.getMessage(), MDC.get(MDCKeys.REQUEST_ID), e);
                                throw new FailedToRollbackS3FileException(e);
                            }
                        }
                    }
                }
        );

        return uploadedFile;
    }
}
