package com.example.blogs.app.storage;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class S3KeyGeneratorTest {

    private S3KeyGenerator s3KeyGenerator;

    @BeforeEach
    void setUp() {
        s3KeyGenerator = new S3KeyGenerator();
    }

    @Test
    void generateKey_shouldReturnCorrectKey_whenFilePathDoesNotEndWithSlash() {
        String filePath = "images";
        String fileName = "photo";
        String extension = ".jpg";

        String key = s3KeyGenerator.generateKey(filePath, fileName, extension);

        assertThat(key).isEqualTo("images/photo.jpg");
    }

    @Test
    void generateKey_shouldReturnCorrectKey_whenFilePathEndsWithSlash() {
        String filePath = "documents/";
        String fileName = "report";
        String extension = ".pdf";

        String key = s3KeyGenerator.generateKey(filePath, fileName, extension);

        assertThat(key).isEqualTo("documents/report.pdf");
    }
}
