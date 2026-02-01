package com.example.blogs.app.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
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

    /**
     * Creates an S3 client configured for local development with custom endpoint.
     * Active only in the local profile for integration with LocalStack or similar services.
     *
     * @param region the AWS region
     * @param accessKey the AWS access key
     * @param secretKey the AWS secret key
     * @param endpoint the custom S3 endpoint URL
     * @return configured S3Client instance with path-style access enabled
     */
    @Bean
    @Profile("local")
    public S3Client localS3Client(
            @Value("${aws.region}") String region,
            @Value("${aws.accessKey}") String accessKey,
            @Value("${aws.secretKey}") String secretKey,
            @Value("${aws.endpoint}") String endpoint
    ) {
        return S3Client.builder()
                .region(Region.of(region))
                .credentialsProvider(
                        StaticCredentialsProvider.create(
                                AwsBasicCredentials.create(accessKey, secretKey)
                        )
                )
                .endpointOverride(URI.create(endpoint))
                .serviceConfiguration(S3Configuration.builder()
                        .pathStyleAccessEnabled(true)
                        .build())
                .build();
    }

    /**
     * Creates an S3 client configured for production with default AWS credentials.
     * Active only in the production profile and uses IAM roles or default credential chain.
     *
     * @param region the AWS region
     * @return configured S3Client instance with default credentials
     */
    @Bean
    @Profile("prod")
    public S3Client prodS3Client(
            @Value("${aws.region}") String region
//            @Value("${aws.accessKey}") String accessKey,
//            @Value("${aws.secretKey}") String secretKey
    ) {
        return S3Client.builder()
                .region(Region.of(region))
//                .credentialsProvider(
//                        StaticCredentialsProvider.create(
//                                AwsBasicCredentials.create(accessKey, secretKey)
//                        )
//                )
                .build();
    }
}
