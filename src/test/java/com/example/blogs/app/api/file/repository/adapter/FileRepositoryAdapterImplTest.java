package com.example.blogs.app.api.file.repository.adapter;

import com.example.blogs.app.api.file.entity.FileEntity;
import com.example.blogs.app.api.file.exception.FailedToDeleteFileByIdException;
import com.example.blogs.app.api.file.exception.FailedToSaveFileException;
import com.example.blogs.app.api.file.fixture.FileFixtures;
import com.example.blogs.app.api.file.repository.FileRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FileRepositoryAdapterImplTest {

    @Mock
    private FileRepository fileRepository;

    private FileRepositoryAdapter fileRepositoryAdapter;

    @BeforeEach
    void setUp() {
        fileRepositoryAdapter = new FileRepositoryAdapterImpl(fileRepository);
    }

    @Test
    void save_shouldSaveAndReturnFileEntity() {
        LocalDateTime now = LocalDateTime.now().withNano(0);
        FileEntity mockFile = FileFixtures.file(1L, now);
        when(fileRepository.save(any(FileEntity.class))).thenReturn(mockFile);
        FileEntity fileToSave = FileFixtures.file();

        FileEntity savedFile = fileRepositoryAdapter.save(
                fileToSave.getFilePath(),
                fileToSave.getFileName(),
                fileToSave.getFileExtension(),
                fileToSave.getUuid()
        );

        assertThat(savedFile.getId()).isEqualTo(1L);
        assertThat(savedFile.getFilePath()).isEqualTo(fileToSave.getFilePath());
        assertThat(savedFile.getFileName()).isEqualTo(fileToSave.getFileName());
        assertThat(savedFile.getFileExtension()).isEqualTo(fileToSave.getFileExtension());
        assertThat(savedFile.getUuid()).isEqualTo(fileToSave.getUuid());
        assertThat(savedFile.getCreatedAt().withNano(0)).isEqualTo(now);
        verify(fileRepository, times(1)).save(any(FileEntity.class));
    }

    @Test
    void save_shouldThrowFailedToSaveFileException_whenRepositoryThrowsException() {
        when(fileRepository.save(any(FileEntity.class))).thenThrow(new RuntimeException("DB error"));

        assertThatThrownBy(() -> fileRepositoryAdapter.save(
                "filePath",
                "fileName",
                "fileExtension",
                "uuid"
        ))
                .isInstanceOf(FailedToSaveFileException.class)
                .hasMessage("Failed to save file");

        verify(fileRepository, times(1)).save(any(FileEntity.class));
    }

    @Test
    void deleteById_shouldInvokeRepositoryDeleteById() {
        Long fileId = 1L;

        fileRepositoryAdapter.deleteById(fileId);

        verify(fileRepository, times(1)).deleteById(fileId);
    }

    @Test
    void deleteBydId_shouldThrowFailedToDeleteFileByIdException_whenRepositoryThrowsException() {
        Long fileId = 1L;
        doThrow(new RuntimeException("DB error"))
                .when(fileRepository).deleteById(fileId);

        assertThatThrownBy(() -> fileRepositoryAdapter.deleteById(fileId))
                .isInstanceOf(FailedToDeleteFileByIdException.class)
                .hasMessage("Failed to delete file by ID");

        verify(fileRepository, times(1)).deleteById(fileId);
    }
}
