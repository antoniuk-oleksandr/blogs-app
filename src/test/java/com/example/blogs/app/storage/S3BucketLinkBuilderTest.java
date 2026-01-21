package com.example.blogs.app.storage;

import com.example.blogs.app.api.file.entity.FileEntity;
import com.example.blogs.app.api.file.fixture.FileFixtures;
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
        FileEntity fileEntity = FileFixtures.file();

        s3BucketLinkBuilder = new S3BucketLinkBuilder(
                "s3BaseUrl",
                "awsEndpoint",
                "bucketName",
                urlUtils
        );
        String link = s3BucketLinkBuilder.buildLink(
                fileEntity.getFilePath(),
                fileEntity.getUuid(),
                fileEntity.getFileExtension()
        );

        assertThat(link).isEqualTo("link/bucketName/" +
                fileEntity.getFilePath() + "/" +
                fileEntity.getUuid() +
                fileEntity.getFileExtension());
        verify(urlUtils).resolveBaseUrl("s3BaseUrl", "awsEndpoint");
    }
}
