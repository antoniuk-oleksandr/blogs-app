package com.example.blogs.app.util;

import org.springframework.stereotype.Component;

/**
 * Utility class for URL manipulation and resolution.
 */
@Component
public class UrlUtils {

    /**
     * Resolves the base URL by preferring custom endpoint over S3 base URL.
     * Ensures the resolved URL ends with a trailing slash.
     *
     * @param s3BaseUrl   the default S3 base URL
     * @param awsEndpoint optional custom AWS endpoint
     * @return resolved base URL with trailing slash
     */
    public String resolveBaseUrl(String s3BaseUrl, String awsEndpoint) {
            if (awsEndpoint != null && !awsEndpoint.isBlank()) {
            return awsEndpoint.endsWith("/") ? awsEndpoint : awsEndpoint + "/";
        }
        return s3BaseUrl.endsWith("/") ? s3BaseUrl : s3BaseUrl + "/";
    }
}
