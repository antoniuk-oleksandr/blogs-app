package com.example.blogs.app.storage;

import com.example.blogs.app.api.file.exception.FailedToDeleteFileException;
import com.example.blogs.app.logging.MDCKeys;
import com.example.blogs.app.storage.exception.FailedToStoreFileException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.core.sync.RequestBody;

/**
 * Implementation of S3 bucket operations using AWS SDK for file uploads.
 */
@Service
public class S3BucketServiceImpl implements S3BucketService {

    private static final Logger log = LoggerFactory.getLogger(S3BucketServiceImpl.class);

    private final S3Client s3Client;

    private final String bucketName;

    private final S3KeyGenerator s3KeyGenerator;

    /**
     * Constructs S3 bucket service with configured client and bucket name.
     *
     * @param s3Client       the configured S3 client
     * @param bucketName     the name of the S3 bucket to use
     * @param s3KeyGenerator the S3 key generator for constructing object keys
     */
    public S3BucketServiceImpl(
            S3Client s3Client,
            @Value("${aws.s3.bucketName}") String bucketName,
            S3KeyGenerator s3KeyGenerator
    ) {
        this.s3Client = s3Client;
        this.bucketName = bucketName;
        this.s3KeyGenerator = s3KeyGenerator;
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
        String s3Key = s3KeyGenerator.generateKey(filePath, fileName, extension);

        PutObjectRequest putRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(s3Key)
                .contentType(contentType)
                .build();

        try {
            s3Client.putObject(putRequest, RequestBody.fromBytes(fileData));
            log.info("s3_upload_success s3Key={} size={} userId={} requestId={}",
                    s3Key, fileData.length, MDC.get(MDCKeys.USER_ID), MDC.get(MDCKeys.REQUEST_ID));
        } catch (Exception e) { 
            log.error("s3_upload_failed s3Key={} error={} userId={} requestId={}",
                    s3Key, e.getMessage(), MDC.get(MDCKeys.USER_ID), MDC.get(MDCKeys.REQUEST_ID), e);
            throw new FailedToStoreFileException(e);
        }
    }

    /**
     * Deletes a file from the S3 bucket by constructing its key and sending a delete request.
     *
     * @param filePath  the storage path within the bucket
     * @param fileName  the file name without extension
     * @param extension the file extension including the dot (e.g., ".png")
     * @throws FailedToDeleteFileException if the S3 delete operation fails
     */
    @Override
    public void delete(String filePath, String fileName, String extension) {
        String s3Key = s3KeyGenerator.generateKey(filePath, fileName, extension);

        DeleteObjectRequest deleteRequest = DeleteObjectRequest.builder()
                .bucket(bucketName)
                .key(s3Key)
                .build();

        try {
            s3Client.deleteObject(deleteRequest);
            log.info("s3_delete_success s3Key={} userId={} requestId={}",
                    s3Key, MDC.get(MDCKeys.USER_ID), MDC.get(MDCKeys.REQUEST_ID));
        } catch (Exception e) { 
            log.error("s3_delete_failed s3Key={} error={} userId={} requestId={}",
                    s3Key, e.getMessage(), MDC.get(MDCKeys.USER_ID), MDC.get(MDCKeys.REQUEST_ID), e);
            throw new FailedToDeleteFileException(e);
        }
    }
}
