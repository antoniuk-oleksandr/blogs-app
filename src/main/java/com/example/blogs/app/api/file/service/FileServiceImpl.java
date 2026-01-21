package com.example.blogs.app.api.file.service;

import com.example.blogs.app.api.file.entity.FileEntity;
import com.example.blogs.app.api.file.exception.FailedToUploadFileException;
import com.example.blogs.app.api.file.repository.adapter.FileRepositoryAdapter;
import com.example.blogs.app.storage.S3BucketService;
import com.example.blogs.app.util.FileNameParts;
import com.example.blogs.app.util.FileUtils;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@Service
@AllArgsConstructor
public class FileServiceImpl implements FileService {

    private final FileRepositoryAdapter fileRepositoryAdapter;

    private final S3BucketService s3BucketService;

    private final FileUtils fileUtils;

    @Override
    @SneakyThrows
    public FileEntity upload(MultipartFile file, String filePath) {
        FileNameParts parts = fileUtils.extractFileNameParts(file.getOriginalFilename());
        String uuid = UUID.randomUUID().toString();
        String normalizedPath = fileUtils.normalizePath(filePath);
        String contentType = fileUtils.detectContentType(parts.extension());

        try {
            s3BucketService.upload(normalizedPath, uuid, parts.extension(), contentType, file.getBytes());
        } catch (Exception e) {
            throw new FailedToUploadFileException(e);
        }

        return fileRepositoryAdapter.save(normalizedPath, parts.name(), parts.extension(), uuid);
    }
}
