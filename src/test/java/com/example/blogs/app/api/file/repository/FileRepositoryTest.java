package com.example.blogs.app.api.file.repository;

import com.example.blogs.app.api.file.entity.FileEntity;
import com.example.blogs.app.api.file.fixture.FileFixtures;
import com.example.blogs.app.support.AbstractPostgresTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import static org.assertj.core.api.Assertions.*;

@DataJpaTest
class FileRepositoryTest extends AbstractPostgresTest {

    @Autowired
    private FileRepository fileRepository;

    @Test
    void save_shouldSaveAndReturnFileEntity() {
        LocalDateTime beforeSave = LocalDateTime.now().withNano(0);
        FileEntity fileToSave = FileFixtures.file();

        FileEntity savedFile = fileRepository.save(fileToSave);

        assertThat(savedFile.getId()).isPositive();
        assertThat(savedFile.getFilePath()).isEqualTo(fileToSave.getFilePath());
        assertThat(savedFile.getFileName()).isEqualTo(fileToSave.getFileName());
        assertThat(savedFile.getFileExtension()).isEqualTo(fileToSave.getFileExtension());
        assertThat(savedFile.getUuid()).isEqualTo(fileToSave.getUuid());
        assertThat(savedFile.getCreatedAt().withNano(0))
                .isCloseTo(beforeSave, within(5, ChronoUnit.SECONDS));
    }
}
