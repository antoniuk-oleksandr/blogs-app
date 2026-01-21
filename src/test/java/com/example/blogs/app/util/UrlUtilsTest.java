package com.example.blogs.app.util;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class UrlUtilsTest {

    private UrlUtils urlUtils;

    @BeforeEach
    void setUp() {
        urlUtils = new UrlUtils();
    }

    @Test
    void resolveBaseUrl_shouldReturnS3BaseUrl_whenAwsEndpointIsNullAndS3BaseUrlEndsWithoutSlash() {
        String result = urlUtils.resolveBaseUrl("link", null);

        assertThat(result).isEqualTo("link/");
    }

    @Test
    void resolveBaseUrl_shouldReturnS3BaseUrl_whenAwsEndpointIsNullAndS3BaseUrlEndsWithSlash() {
        String result = urlUtils.resolveBaseUrl("link/", null);

        assertThat(result).isEqualTo("link/");
    }

    @Test
    void resolveBaseUrl_shouldReturnS3BaseUrl_whenAwsEndpointIsBlankAnd() {
        String result = urlUtils.resolveBaseUrl("link/", "");

        assertThat(result).isEqualTo("link/");
    }

    @Test
    void resolveBaseUrl_shouldReturnAwsEndpoint_whenAwdEndpointIsValidAndEndsWithoutSlash() {
        String result = urlUtils.resolveBaseUrl("", "link");

        assertThat(result).isEqualTo("link/");
    }

    @Test
    void resolveBaseUrl_shouldReturnAwsEndpoint_whenAwdEndpointIsValidAndEndsWithSlash() {
        String result = urlUtils.resolveBaseUrl("", "link/");

        assertThat(result).isEqualTo("link/");
    }
}
