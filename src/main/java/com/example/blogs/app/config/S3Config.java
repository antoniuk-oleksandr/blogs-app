package com.example.blogs.app.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3ClientBuilder;
import software.amazon.awssdk.services.s3.S3Configuration;

import java.net.URI;

/**
 * Configuration class for AWS S3 client with support for custom endpoints and path-style access.
 */
@Configuration
public class S3Config {

    private final String region;

    private final String accessKey;

    private final String secretKey;

    private final String endpoint;

    /**
     * Constructs S3 configuration with AWS credentials and endpoint settings.
     *
     * @param region    the AWS region for the S3 bucket
     * @param accessKey the AWS access key for authentication
     * @param secretKey the AWS secret key for authentication
     * @param endpoint  optional custom endpoint URL for S3-compatible services
     */
    public S3Config(
            @Value("${aws.region}") String region,
            @Value("${aws.accessKey}") String accessKey,
            @Value("${aws.secretKey}") String secretKey,
            @Value("${aws.endpoint}") String endpoint
    ) {
        this.region = region;
        this.accessKey = accessKey;
        this.secretKey = secretKey;
        this.endpoint = endpoint;
    }

    /**
     * Creates and configures an S3 client bean with credentials and optional endpoint override.
     * Enables path-style access when a custom endpoint is provided.
     *
     * @return configured S3 client ready for use
     */
    @Bean
    public S3Client s3Client() {
        S3ClientBuilder builder = S3Client.builder()
                .region(Region.of(region))
                .credentialsProvider(
                        StaticCredentialsProvider.create(
                                AwsBasicCredentials.create(accessKey, secretKey)
                        )
                );

        if (endpoint != null && !endpoint.isEmpty()) {
            builder.endpointOverride(URI.create(endpoint))
                    .serviceConfiguration(S3Configuration.builder()
                            .pathStyleAccessEnabled(true)
                            .build());
        }

        return builder.build();
    }
}
