package com.example.blogs.app.api.file.repository.adapter;

import com.example.blogs.app.api.file.entity.FileEntity;
import com.example.blogs.app.api.file.exception.FailedToDeleteFileByIdException;
import com.example.blogs.app.api.file.exception.FailedToSaveFileException;
import com.example.blogs.app.api.file.repository.FileRepository;
import com.example.blogs.app.logging.MDCKeys;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;

/**
 * Wraps file repository operations with exception translation for consistent error handling.
 */
@Component
@AllArgsConstructor
public class FileRepositoryAdapterImpl implements FileRepositoryAdapter {

    private static final Logger log = LoggerFactory.getLogger(FileRepositoryAdapterImpl.class);

    private final FileRepository fileRepository;

    /**
     * Saves file metadata to the repository with exception translation.
     * Builds a file entity from the provided metadata and persists it.
     *
     * @param filePath  the storage path of the file
     * @param fileName  the original name of the file
     * @param extension the file extension including the dot (e.g., ".png")
     * @param uuid      the unique identifier for the file
     * @return the saved file entity with generated ID
     * @throws FailedToSaveFileException if the repository operation fails
     */
    @Override
    public FileEntity save(String filePath, String fileName, String extension, String uuid) {
        FileEntity file = FileEntity.builder()
                .filePath(filePath)
                .fileName(fileName)
                .fileExtension(extension)
                .uuid(uuid)
                .build();

        try {
            return fileRepository.save(file);
        } catch (Exception e) { 
            log.error("database_operation_failed operation=save fileName={} fileId={} error={} requestId={}",
                    fileName, uuid, e.getMessage(), MDC.get(MDCKeys.REQUEST_ID), e);
            throw new FailedToSaveFileException(e);
        }
    }

    /**
     * Deletes file metadata by its ID with exception translation.
     *
     * @param fileId the ID of the file entity to delete
     * @throws FailedToDeleteFileByIdException if the repository operation fails
     */
    @Override
    public void deleteById(Long fileId) {
        try {
            fileRepository.deleteById(fileId);
        } catch (Exception e) { 
            log.error("database_operation_failed operation=deleteById fileId={} error={} requestId={}",
                    fileId, e.getMessage(), MDC.get(MDCKeys.REQUEST_ID), e);
            throw new FailedToDeleteFileByIdException(e);
        }
    }
}
