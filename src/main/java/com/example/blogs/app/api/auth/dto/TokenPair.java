package com.example.blogs.app.api.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * JWT authentication token pair returned after successful registration or login.
 *
 * @param accessToken short-lived token for API authentication (15 minutes)
 * @param refreshToken long-lived token for obtaining new access tokens (30 days)
 */
@Schema(description = "JWT authentication token pair")
public record TokenPair(
        @Schema(
                description = "Short-lived access token for API authentication (valid for 15 minutes)",
                example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJqb2huZG9lIn0.signature"
        )
        String accessToken,

        @Schema(
                description = "Long-lived refresh token for obtaining new access tokens (valid for 30 days)",
                example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJqb2huZG9lIn0.signature"
        )
        String refreshToken
) {
}
