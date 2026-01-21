package com.example.blogs.app.api.file.service;

import com.example.blogs.app.api.file.entity.FileEntity;
import org.springframework.web.multipart.MultipartFile;

public interface FileService {
    FileEntity upload(MultipartFile file, String filePath);
}
