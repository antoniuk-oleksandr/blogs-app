package com.example.blogs.app.storage;

public interface S3BucketService {
    void upload(
            String filePath,
            String fileName,
            String extension,
            String contentType,
            byte[] fileData
    );
}
