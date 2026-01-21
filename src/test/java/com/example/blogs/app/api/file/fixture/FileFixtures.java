package com.example.blogs.app.api.file.fixture;

import com.example.blogs.app.api.file.entity.FileEntity;

import java.time.LocalDateTime;

public class FileFixtures {

    public static FileEntity file() {
        return FileEntity.builder()
                .filePath("filePath")
                .fileName("fileName")
                .fileExtension("fileExtension")
                .uuid("uuid")
                .build();
    }

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
