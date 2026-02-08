package com.example.blogs.app.api.file.service;

import com.example.blogs.app.api.file.entity.FileEntity;
import com.example.blogs.app.api.file.fixture.FileFixtures;
import com.example.blogs.app.storage.FileLinkBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FileUrlBuilderTest {

    @Mock
    private FileLinkBuilder fileLinkBuilder;

    private FileUrlBuilder fileUrlBuilder;

    @BeforeEach
    void setUp() {
        fileUrlBuilder = new FileUrlBuilder(fileLinkBuilder);
    }

    @Test
    void build_shouldBuildFileUrl() {
        Long fileId = 1L;
        LocalDateTime now = LocalDateTime.now().withNano(0);
        FileEntity file = FileFixtures.file(fileId, now);

        when(fileLinkBuilder.buildLink(anyString(), anyString(), anyString()))
                .thenReturn("link");

        String result = fileUrlBuilder.build(file);

        assertThat(result).isEqualTo("link");
        verify(fileLinkBuilder, times(1))
                .buildLink("filePath", "uuid", "fileExtension");
    }
}
