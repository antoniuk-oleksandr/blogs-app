package com.example.blogs.app.util;

import org.springframework.stereotype.Component;

@Component
public class UrlUtils {

    public String resolveBaseUrl(String s3BaseUrl, String awsEndpoint) {
            if (awsEndpoint != null && !awsEndpoint.isBlank()) {
            return awsEndpoint.endsWith("/") ? awsEndpoint : awsEndpoint + "/";
        }
        return s3BaseUrl.endsWith("/") ? s3BaseUrl : s3BaseUrl + "/";
    }
}
