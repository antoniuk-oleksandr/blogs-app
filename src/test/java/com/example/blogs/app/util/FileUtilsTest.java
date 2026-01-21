package com.example.blogs.app.util;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.assertj.core.api.Assertions.*;

class FileUtilsTest {

    private FileUtils fileUtils;

    @BeforeEach
    void setUp() {
        fileUtils = new FileUtils();
    }

    @ParameterizedTest
    @CsvSource({
            ".png, image/png",
            ".jpg, image/jpeg",
            ".gif, image/gif",
            ".txt, application/octet-stream",
    })
    void detectContentType_shouldReturnContentType(String extension, String expectedContentType) {
        String result = fileUtils.detectContentType(extension);

        assertThat(result).isEqualTo(expectedContentType);
    }

    @ParameterizedTest
    @CsvSource({
            "image.png, image, .png",
            "archive.tar.gz, archive.tar, .gz",
            "filename, filename, ''",
            ".gitignore, .gitignore, ''",
    })
    void extractFileNameParts_shouldSplitNameAndExtension(
            String input,
            String expectedName,
            String expectedExtension
    ) {
        FileNameParts result = fileUtils.extractFileNameParts(input);

        assertThat(result.name()).isEqualTo(expectedName);
        assertThat(result.extension()).isEqualTo(expectedExtension);
    }

    @ParameterizedTest
    @CsvSource({
            "path, path",
            "/path, path"
    })
    void normalizePath_should(String filePath, String expectedNormalizedPath) {
        String result = fileUtils.normalizePath(filePath);

        assertThat(result).isEqualTo(expectedNormalizedPath);
    }
}
