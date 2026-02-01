package com.example.blogs.app.storage;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

/**
 * Production implementation of FileLinkBuilder for constructing S3 file access URLs.
 * Active only in the production profile.
 */
@Service
@Profile("prod")
public class S3BucketLinkBuilderProd implements FileLinkBuilder {

    private final String s3BaseUrl;

    /**
     * Constructs S3 link builder with resolved base URL and bucket configuration.
     *
     * @param s3BaseUrl the S3 base URL from configuration
     */
    public S3BucketLinkBuilderProd(@Value("${aws.s3.base-url}") String s3BaseUrl) {
        this.s3BaseUrl = s3BaseUrl;
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
