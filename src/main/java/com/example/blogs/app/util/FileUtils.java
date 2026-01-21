package com.example.blogs.app.util;

import org.springframework.stereotype.Component;

@Component
public class FileUtils {

    public String detectContentType(String extension) {
        return switch (extension) {
            case ".png" -> "image/png";
            case ".jpg" -> "image/jpeg";
            case ".gif" -> "image/gif";
            default -> "application/octet-stream";
        };
    }

    public FileNameParts extractFileNameParts(String originalFileName) {
        int dotIndex = originalFileName.lastIndexOf('.');
        if (dotIndex > 0) {
            return new FileNameParts(originalFileName.substring(0, dotIndex),
                    originalFileName.substring(dotIndex));
        } else {
            return new FileNameParts(originalFileName, "");
        }
    }

    public String normalizePath(String filePath) {
        return filePath.startsWith("/") ? filePath.substring(1) : filePath;
    }
}
