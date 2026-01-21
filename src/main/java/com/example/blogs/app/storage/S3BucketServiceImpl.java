package com.example.blogs.app.storage;

import com.example.blogs.app.storage.exception.FailedToStoreFileException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.core.sync.RequestBody;

/**
 * Implementation of S3 bucket operations using AWS SDK for file uploads.
 */
@Service
public class S3BucketServiceImpl implements S3BucketService {

    private final S3Client s3Client;

    private final String bucketName;

    /**
     * Constructs S3 bucket service with configured client and bucket name.
     *
     * @param s3Client   the configured S3 client
     * @param bucketName the name of the S3 bucket to use
     */
    public S3BucketServiceImpl(
            S3Client s3Client,
            @Value("${aws.s3.bucketName}") String bucketName
    ) {
        this.s3Client = s3Client;
        this.bucketName = bucketName;
    }

    /**
     * Uploads a file to the S3 bucket with specified metadata.
     * Constructs the S3 key from path and filename, then uploads with content type.
     *
     * @param filePath    the storage path within the bucket
     * @param fileName    the file name without extension
     * @param extension   the file extension including the dot (e.g., ".png")
     * @param contentType the MIME type of the file
     * @param fileData    the file content as byte array
     * @throws FailedToStoreFileException if the S3 upload operation fails
     */
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
            throw new FailedToStoreFileException(e);
        }
    }
}
