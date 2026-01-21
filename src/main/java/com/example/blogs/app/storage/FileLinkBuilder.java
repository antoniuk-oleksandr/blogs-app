package com.example.blogs.app.storage;

import com.example.blogs.app.api.file.entity.FileEntity;

public interface FileLinkBuilder {
    String buildLink(FileEntity fileEntity);
}
