package com.example.blogs.app.storage;

import org.springframework.stereotype.Component;

@Component
public class S3KeyGenerator {

    public String generateKey(String filePath, String fileName, String extension) {
       return  (filePath.endsWith("/") ? filePath + fileName : filePath + "/" + fileName) + extension;
    }
}
