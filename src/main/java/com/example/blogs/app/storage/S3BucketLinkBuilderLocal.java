package com.example.blogs.app.storage;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

/**
 * Builds S3 file access links by combining base URL, bucket name, and file metadata.
 */
@Service
@Profile("local")
public class S3BucketLinkBuilderLocal implements FileLinkBuilder {

    private final String s3BaseUrl;

    /**
     * Constructs S3 link builder with resolved base URL and bucket configuration.
     *
     * @param awsEndpoint optional custom AWS endpoint
     * @param bucketName  the S3 bucket name
     */
    public S3BucketLinkBuilderLocal(
            @Value("${aws.endpoint}") String awsEndpoint,
            @Value("${aws.s3.bucket-name}") String bucketName
    ) {
        this.s3BaseUrl = awsEndpoint + "/" + bucketName;
    }

    /**
     * Builds a complete S3 URL for accessing a file.
     * Combines base URL, bucket name, file path, UUID, and extension.
     *
     * @param filePath      the storage path of the file
     * @param uuid          the unique identifier of the file
     * @param fileExtension the file extension including the dot (e.g., ".png")
     * @return the complete S3 file access URL
     */
    @Override
    public String buildLink(
            String filePath,
            String uuid,
            String fileExtension
    ) {
        return s3BaseUrl + "/" + filePath + "/" + uuid + fileExtension;
    }
}
