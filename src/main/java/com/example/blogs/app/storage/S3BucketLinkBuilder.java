package com.example.blogs.app.storage;

import com.example.blogs.app.api.file.entity.FileEntity;
import com.example.blogs.app.util.UrlUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class S3BucketLinkBuilder implements FileLinkBuilder {

    private final String s3BaseUrl;

    private final String s3BucketName;

    public S3BucketLinkBuilder(
            @Value("${aws.s3.base-url}") String s3BaseUrl,
            @Value("${aws.endpoint}") String awsEndpoint,
            @Value("${aws.s3.bucketName}") String s3BucketName,
            UrlUtils urlUtils
    ) {
        this.s3BaseUrl = urlUtils.resolveBaseUrl(s3BaseUrl, awsEndpoint);
        this.s3BucketName = s3BucketName;
    }

    @Override
    public String buildLink(FileEntity fileEntity) {
        return s3BaseUrl + s3BucketName + "/" +
                fileEntity.getFilePath() + "/" +
                fileEntity.getUuid() +
                fileEntity.getFileExtension();
    }
}
