package com.example.blogs.app.api.file.repository.adapter;

import com.example.blogs.app.api.file.entity.FileEntity;

public interface FileRepositoryAdapter {
    FileEntity save(String filePath, String fileName, String extension, String uuid);
}
