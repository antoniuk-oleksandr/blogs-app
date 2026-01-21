package com.example.blogs.app.storage;

import com.example.blogs.app.api.file.exception.FailedToSaveFileException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.core.sync.RequestBody;

@Service
public class S3BucketServiceImpl implements S3BucketService {

    private final S3Client s3Client;

    private final String bucketName;

    public S3BucketServiceImpl(
            S3Client s3Client,
            @Value("${aws.s3.bucketName}") String bucketName
    ) {
        this.s3Client = s3Client;
        this.bucketName = bucketName;
    }

    @Override
    public void upload(
            String filePath,
            String fileName,
            String extension,
            String contentType,
            byte[] fileData
    ) {
        String s3Key = (filePath.endsWith("/") ? filePath + fileName : filePath + "/" + fileName) + extension;

        PutObjectRequest putRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(s3Key)
                .contentType(contentType)
                .build();

        try {
            s3Client.putObject(putRequest, RequestBody.fromBytes(fileData));
        } catch (Exception e) {
            throw new FailedToSaveFileException(e);
        }
    }
}
