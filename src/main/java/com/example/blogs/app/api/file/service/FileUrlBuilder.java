package com.example.blogs.app.api.file.service;

import com.example.blogs.app.api.file.entity.FileEntity;
import com.example.blogs.app.storage.FileLinkBuilder;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class FileUrlBuilder {

    private final FileLinkBuilder fileLinkBuilder;

    public String build(FileEntity file) {
        return fileLinkBuilder.buildLink(
                file.getFilePath(),
                file.getUuid(),
                file.getFileExtension()
        );
    }
}