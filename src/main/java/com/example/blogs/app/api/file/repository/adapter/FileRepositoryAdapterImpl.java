package com.example.blogs.app.api.file.repository.adapter;

import com.example.blogs.app.api.file.entity.FileEntity;
import com.example.blogs.app.api.file.exception.FailedToSaveFileException;
import com.example.blogs.app.api.file.repository.FileRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class FileRepositoryAdapterImpl implements FileRepositoryAdapter {

    private final FileRepository fileRepository;

    @Override
    public FileEntity save(String filePath, String fileName, String extension, String uuid) {
        FileEntity file = FileEntity.builder()
                .filePath(filePath)
                .fileName(fileName)
                .fileExtension(extension)
                .uuid(uuid)
                .build();

        try {
            return fileRepository.save(file);
        } catch (Exception e) {
            throw new FailedToSaveFileException(e);
        }
    }
}
