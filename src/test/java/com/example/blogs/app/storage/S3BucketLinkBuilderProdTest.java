package com.example.blogs.app.storage;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class S3BucketLinkBuilderProdTest {

    @Test
    void buildLink_shouldReturnCorrectS3Link() {
        String baseUrl = "link";
        FileLinkBuilder fileLinkBuilder = new S3BucketLinkBuilderProd(baseUrl);

        String link = fileLinkBuilder.buildLink(
                "filePath",
                "uuid",
                "fileExtension"
        );

        assertThat(link).isEqualTo(
                "link" + "/" +
                        "filePath" + "/" +
                        "uuid" +
                        "fileExtension"
        );
    }
}
