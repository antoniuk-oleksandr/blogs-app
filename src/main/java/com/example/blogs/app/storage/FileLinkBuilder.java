package com.example.blogs.app.storage;

public interface FileLinkBuilder {
    String buildLink(String filePath, String uuid, String fileExtension);
}
