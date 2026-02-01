package com.example.blogs.app.storage;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class S3BucketLinkBuilderLocalTest {

    @Test
    void buildLink_shouldReturnCorrectS3Link() {
        FileLinkBuilder fileLinkBuilder = new S3BucketLinkBuilderLocal(
                "link",
                "bucketName"
        );
        String link = fileLinkBuilder.buildLink(
                "filePath",
                "uuid",
                "fileExtension"
        );

        assertThat(link).isEqualTo(
                "link" + "/" +
                        "bucketName" + "/" +
                        "filePath" + "/" +
                        "uuid" +
                        "fileExtension"
        );
    }
}
