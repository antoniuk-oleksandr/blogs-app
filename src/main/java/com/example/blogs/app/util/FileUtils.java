package com.example.blogs.app.util;

import org.springframework.stereotype.Component;

/**
 * Utility class for file-related operations including content type detection, filename parsing, and path normalization.
 */
@Component
public class FileUtils {

    /**
     * Detects the MIME content type based on file extension.
     *
     * @param extension the file extension including the dot (e.g., ".png")
     * @return the MIME type string, or "application/octet-stream" if unknown
     */
    public String detectContentType(String extension) {
        return switch (extension) {
            case ".png" -> "image/png";
            case ".jpg" -> "image/jpeg";
            case ".gif" -> "image/gif";
            default -> "application/octet-stream";
        };
    }

    /**
     * Extracts the name and extension parts from a filename.
     *
     * @param originalFileName the original filename to parse
     * @return file name parts with name and extension separated
     */
    public FileNameParts extractFileNameParts(String originalFileName) {
        int dotIndex = originalFileName.lastIndexOf('.');
        if (dotIndex > 0) {
            return new FileNameParts(originalFileName.substring(0, dotIndex),
                    originalFileName.substring(dotIndex));
        } else {
            return new FileNameParts(originalFileName, "");
        }
    }

    /**
     * Normalizes a file path by removing leading slash if present.
     *
     * @param filePath the file path to normalize
     * @return normalized file path without leading slash
     */
    public String normalizePath(String filePath) {
        return filePath.startsWith("/") ? filePath.substring(1) : filePath;
    }
}
