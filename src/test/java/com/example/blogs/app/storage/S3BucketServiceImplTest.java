package com.example.blogs.app.storage;

import com.example.blogs.app.api.file.exception.FailedToDeleteFileException;
import com.example.blogs.app.storage.exception.FailedToStoreFileException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class S3BucketServiceImplTest {

    @Mock
    private S3Client s3Client;

    @Mock
    private S3KeyGenerator keyGenerator;

    private S3BucketService s3BucketService;

    private final String bucketName = "my-bucket";

    @BeforeEach
    void setUp() {
        s3BucketService = new S3BucketServiceImpl(s3Client, bucketName, keyGenerator);
    }

    @Test
    void upload_shouldUploadFileToS3Bucket() {
        String filePath = "folder";
        String fileName = "file";
        String extension = ".txt";
        String contentType = "text/plain";
        byte[] fileData = "hello".getBytes();

        when(keyGenerator.generateKey(anyString(), anyString(), anyString())).thenReturn("folder/file.txt");

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

        when(keyGenerator.generateKey(anyString(), anyString(), anyString())).thenReturn("folder/file.txt");

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

    @Test
    void delete_shouldDeleteFileFromS3Bucket() {
        String filePath = "folder";
        String fileName = "file";
        String extension = ".txt";

        when(keyGenerator.generateKey(anyString(), anyString(), anyString()))
                .thenReturn("folder/file.txt");

        s3BucketService.delete(filePath, fileName, extension);

        verify(s3Client).deleteObject(any(DeleteObjectRequest.class));
    }

    @Test
    void delete_shouldThrowFailedToDeleteFileException_whenS3ClientFails() {
        String filePath = "folder";
        String fileName = "file";
        String extension = ".txt";

        when(keyGenerator.generateKey(anyString(), anyString(), anyString()))
                .thenReturn("folder/file.txt");
        when(s3Client.deleteObject(any(DeleteObjectRequest.class)))
                .thenThrow(new FailedToDeleteFileException(null));


        assertThatThrownBy(() -> s3BucketService.delete(filePath, fileName, extension))
                .isInstanceOf(FailedToDeleteFileException.class)
                .hasMessageContaining("Failed to delete file");
    }
}
