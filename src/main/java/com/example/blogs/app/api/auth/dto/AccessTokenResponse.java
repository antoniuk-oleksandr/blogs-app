package com.example.blogs.app.api.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Response payload containing a new access token after refresh token validation.
 *
 * @param accessToken newly generated access token with short expiration (15 min)
 */
@Schema(description = "Response containing a new access token")
public record AccessTokenResponse(
        @Schema(
                description = "Short-lived JWT access token for API authentication",
                example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjMiLCJ1c2VybmFtZSI6ImpvaG5kb2UiLCJpYXQiOjE3MDMyNTYwMDAsImV4cCI6MTcwMzI1NjkwMH0.signature"
        )
        String accessToken
) {
}

