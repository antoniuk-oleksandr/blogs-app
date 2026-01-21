package com.example.blogs.app.storage;

import com.example.blogs.app.util.UrlUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * Builds S3 file access links by combining base URL, bucket name, and file metadata.
 */
@Service
public class S3BucketLinkBuilder implements FileLinkBuilder {

    private final String s3BaseUrl;

    private final String s3BucketName;

    /**
     * Constructs S3 link builder with resolved base URL and bucket configuration.
     *
     * @param s3BaseUrl     the base URL for S3 access
     * @param awsEndpoint   optional custom AWS endpoint
     * @param s3BucketName  the S3 bucket name
     * @param urlUtils      utility for resolving the final base URL
     */
    public S3BucketLinkBuilder(
            @Value("${aws.s3.base-url}") String s3BaseUrl,
            @Value("${aws.endpoint}") String awsEndpoint,
            @Value("${aws.s3.bucketName}") String s3BucketName,
            UrlUtils urlUtils
    ) {
        this.s3BaseUrl = urlUtils.resolveBaseUrl(s3BaseUrl, awsEndpoint);
        this.s3BucketName = s3BucketName;
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
    public String buildLink(String filePath, String uuid, String fileExtension) {
        return s3BaseUrl + s3BucketName + "/" +
                filePath + "/" +
                uuid +
                fileExtension;
    }
}
