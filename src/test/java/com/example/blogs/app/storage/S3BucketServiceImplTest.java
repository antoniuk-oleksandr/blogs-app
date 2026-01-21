package com.example.blogs.app.storage;

import com.example.blogs.app.storage.exception.FailedToStoreFileException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class S3BucketServiceImplTest {

    @Mock
    private S3Client s3Client;

    private S3BucketService s3BucketService;

    private final String bucketName = "my-bucket";

    @BeforeEach
    void setUp() {
        s3BucketService = new S3BucketServiceImpl(s3Client, bucketName);
    }

    @Test
    void upload_shouldUploadFileToS3Bucket() {
        String filePath = "folder";
        String fileName = "file";
        String extension = ".txt";
        String contentType = "text/plain";
        byte[] fileData = "hello".getBytes();

        s3BucketService.upload(filePath, fileName, extension, contentType, fileData);

        ArgumentCaptor<PutObjectRequest> captor = ArgumentCaptor.forClass(PutObjectRequest.class);
        verify(s3Client, times(1)).putObject(captor.capture(), any(RequestBody.class));

        PutObjectRequest request = captor.getValue();
        assertThat(request.bucket()).isEqualTo(bucketName);
        assertThat(request.key()).isEqualTo("folder/file.txt");
        assertThat(request.contentType()).isEqualTo(contentType);
    }

    @Test
    void upload_shouldHandleFilePathWithTrailingSlash() {
        String filePath = "folder/";
        String fileName = "file";
        String extension = ".txt";
        String contentType = "text/plain";
        byte[] fileData = "hello".getBytes();

        s3BucketService.upload(filePath, fileName, extension, contentType, fileData);

        ArgumentCaptor<PutObjectRequest> captor = ArgumentCaptor.forClass(PutObjectRequest.class);
        verify(s3Client).putObject(captor.capture(), any(RequestBody.class));

        PutObjectRequest request = captor.getValue();
        assertThat(request.key()).isEqualTo("folder/file.txt");
    }

    @Test
    void upload_shouldThrowFailedToStoreFileException_whenUploadFails() {
        String filePath = "folder";
        String fileName = "file";
        String extension = ".txt";
        String contentType = "text/plain";
        byte[] fileData = "hello".getBytes();

        doThrow(new RuntimeException("S3 error")).when(s3Client).putObject(any(PutObjectRequest.class), any(RequestBody.class));

        assertThatThrownBy(() -> s3BucketService.upload(filePath, fileName, extension, contentType, fileData))
                .isInstanceOf(FailedToStoreFileException.class)
                .hasMessageContaining("Failed to store file");

        verify(s3Client, times(1)).putObject(any(PutObjectRequest.class), any(RequestBody.class));
    }
}
