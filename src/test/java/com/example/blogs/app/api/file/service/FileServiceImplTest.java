package com.example.blogs.app.api.file.service;

import com.example.blogs.app.api.file.entity.FileEntity;
import com.example.blogs.app.api.file.exception.FailedToUploadFileException;
import com.example.blogs.app.api.file.fixture.FileFixtures;
import com.example.blogs.app.api.file.repository.adapter.FileRepositoryAdapter;
import com.example.blogs.app.storage.S3BucketService;
import com.example.blogs.app.util.FileNameParts;
import com.example.blogs.app.util.FileUtils;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FileServiceImplTest {

    @Mock
    private FileRepositoryAdapter fileRepositoryAdapter;

    @Mock
    private S3BucketService s3BucketService;

    @Mock
    private FileUtils fileUtils;

    @Mock
    private MultipartFile multipartFile;

    private FileService fileService;

    @BeforeEach
    void setUp() {
        fileService = new FileServiceImpl(fileRepositoryAdapter, s3BucketService, fileUtils);
    }

    @Test
    @SneakyThrows
    void upload_shouldUploadFileAndReturnFileEntity() {
        LocalDateTime now = LocalDateTime.now().withNano(0);
        FileNameParts parts = new FileNameParts("image", ".jpg");
        FileEntity mockFile = FileFixtures.file(1L, now);
        when(multipartFile.getOriginalFilename()).thenReturn("image.jpg");
        when(multipartFile.getBytes()).thenReturn(new byte[]{1, 2, 3});
        when(fileUtils.extractFileNameParts(anyString())).thenReturn(parts);
        when(fileUtils.normalizePath(anyString())).thenReturn("path");
        when(fileUtils.detectContentType(anyString())).thenReturn("image/jpeg");
        when(fileRepositoryAdapter.save(anyString(), anyString(), anyString(), anyString()))
                .thenReturn(mockFile);

        FileEntity actualFile = fileService.upload(multipartFile, "path");

        assertThat(actualFile).isEqualTo(mockFile);
        verify(multipartFile, times(1)).getOriginalFilename();
        verify(multipartFile, times(1)).getBytes();
        verify(fileUtils, times(1)).extractFileNameParts("image.jpg");
        verify(fileUtils, times(1)).normalizePath("path");
        verify(fileUtils, times(1)).detectContentType(".jpg");
        verify(s3BucketService, times(1))
                .upload(
                        eq("path"),
                        anyString(),
                        eq(".jpg"),
                        eq("image/jpeg"),
                        eq(new byte[]{1, 2, 3}));
        verify(fileRepositoryAdapter, times(1))
                .save(eq("path"), eq("image"), eq(".jpg"), anyString());
    }

    @Test
    @SneakyThrows
    void upload_shouldThrowFailedToUploadFileException_whenFileBytesCannotBeRetrieved() {
        FileNameParts parts = new FileNameParts("image", ".jpg");
        when(multipartFile.getOriginalFilename()).thenReturn("image.jpg");
        when(multipartFile.getBytes()).thenThrow(new RuntimeException("Failed to get bytes"));
        when(fileUtils.extractFileNameParts(anyString())).thenReturn(parts);
        when(fileUtils.normalizePath(anyString())).thenReturn("path");
        when(fileUtils.detectContentType(anyString())).thenReturn("image/jpeg");

        assertThatThrownBy(() -> fileService.upload(multipartFile, "path"))
                .isInstanceOf(FailedToUploadFileException.class)
                .hasMessageContaining("Failed to upload file");

        verify(multipartFile, times(1)).getOriginalFilename();
        verify(multipartFile, times(1)).getBytes();
        verify(fileUtils, times(1)).extractFileNameParts("image.jpg");
        verify(fileUtils, times(1)).normalizePath("path");
        verify(fileUtils, times(1)).detectContentType(".jpg");
        verify(s3BucketService, never()).upload(anyString(), anyString(), anyString(), anyString(), any());
        verify(fileRepositoryAdapter, never()).save(anyString(), anyString(), anyString(), anyString());
    }

    @Test
    @SneakyThrows
    void upload_shouldThrowFailedToUploadFileException_whenS3ServiceFails() {
        FileNameParts parts = new FileNameParts("image", ".jpg");
        when(multipartFile.getOriginalFilename()).thenReturn("image.jpg");
        when(multipartFile.getBytes()).thenReturn(new byte[]{1, 2, 3});
        when(fileUtils.extractFileNameParts(anyString())).thenReturn(parts);
        when(fileUtils.normalizePath(anyString())).thenReturn("path");
        when(fileUtils.detectContentType(anyString())).thenReturn("image/jpeg");
        doThrow(new RuntimeException("S3 upload failed"))
                .when(s3BucketService)
                .upload(anyString(), anyString(), anyString(), anyString(), any(byte[].class));

        assertThatThrownBy(() -> fileService.upload(multipartFile, "path"))
                .isInstanceOf(FailedToUploadFileException.class)
                .hasMessageContaining("Failed to upload file");

        verify(multipartFile, times(1)).getOriginalFilename();
        verify(multipartFile, times(1)).getBytes();
        verify(fileUtils, times(1)).extractFileNameParts("image.jpg");
        verify(fileUtils, times(1)).normalizePath("path");
        verify(fileUtils, times(1)).detectContentType(".jpg");
        verify(s3BucketService, times(1))
                .upload(
                        eq("path"),
                        anyString(),
                        eq(".jpg"),
                        eq("image/jpeg"),
                        eq(new byte[]{1, 2, 3}));
        verify(fileRepositoryAdapter, never())
                .save(eq("path"), eq("image"), eq(".jpg"), anyString());
    }
}
