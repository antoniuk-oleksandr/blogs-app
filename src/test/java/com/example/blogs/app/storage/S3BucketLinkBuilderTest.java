package com.example.blogs.app.storage;

import com.example.blogs.app.util.UrlUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class S3BucketLinkBuilderTest {

    @Mock
    private UrlUtils urlUtils;

    private FileLinkBuilder s3BucketLinkBuilder;

    @Test
    void buildLink_shouldReturnCorrectS3Link() {
        when(urlUtils.resolveBaseUrl(anyString(), anyString()))
                .thenReturn("link/");

        s3BucketLinkBuilder = new S3BucketLinkBuilder(
                "s3BaseUrl",
                "awsEndpoint",
                "bucketName",
                urlUtils
        );
        String link = s3BucketLinkBuilder.buildLink(
                "filePath",
                "uuid",
                "fileExtension"
        );

        assertThat(link).isEqualTo("link/bucketName/" +
                "filePath" + "/" +
                "uuid" +
                "fileExtension");
        verify(urlUtils).resolveBaseUrl("s3BaseUrl", "awsEndpoint");
    }
}
