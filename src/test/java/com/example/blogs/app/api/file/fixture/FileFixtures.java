package com.example.blogs.app.api.file.fixture;

import com.example.blogs.app.api.file.entity.FileEntity;

import java.time.LocalDateTime;

/**
 * Test fixture factory for creating file entities with predefined values.
 */
public class FileFixtures {

    /**
     * Creates a file entity with default values.
     *
     * @return configured file entity
     */
    public static FileEntity file() {
        return FileEntity.builder()
                .filePath("filePath")
                .fileName("fileName")
                .fileExtension("fileExtension")
                .uuid("uuid")
                .build();
    }

    /**
     * Creates a file entity with the specified ID and timestamp.
     *
     * @param id   the file ID
     * @param time the creation timestamp
     * @return configured file entity
     */
    public static FileEntity file(Long id, LocalDateTime time) {
        return FileEntity.builder()
                .id(id)
                .filePath("filePath")
                .fileName("fileName")
                .fileExtension("fileExtension")
                .uuid("uuid")
                .createdAt(time)
                .build();
    }
}
