package com.example.blogs.app.api.file.repository.adapter;

import com.example.blogs.app.api.file.entity.FileEntity;
import com.example.blogs.app.api.file.exception.FailedToSaveFileException;
import com.example.blogs.app.api.file.repository.FileRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Wraps file repository operations with exception translation for consistent error handling.
 */
@Component
@AllArgsConstructor
public class FileRepositoryAdapterImpl implements FileRepositoryAdapter {

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
            throw new FailedToSaveFileException(e);
        }
    }
}
