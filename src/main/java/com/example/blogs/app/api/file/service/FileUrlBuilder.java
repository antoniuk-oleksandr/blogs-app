package com.example.blogs.app.api.file.service;

import com.example.blogs.app.api.file.entity.FileEntity;
import com.example.blogs.app.storage.FileLinkBuilder;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Builds public URLs for file entities by delegating to the file link builder.
 */
@Component
@AllArgsConstructor
public class FileUrlBuilder {

    private final FileLinkBuilder fileLinkBuilder;

    /**
     * Builds a public URL for accessing the file.
     * Constructs the URL from the file's path, UUID, and extension.
     *
     * @param file the file entity containing metadata
     * @return the public URL for accessing the file
     */
    public String build(FileEntity file) {
        return fileLinkBuilder.buildLink(
                file.getFilePath(),
                file.getUuid(),
                file.getFileExtension()
        );
    }
}