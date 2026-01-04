package com.example.blogs.app.api.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

/**
 * Request payload for refreshing an access token using a valid refresh token.
 *
 * @param refreshToken long-lived refresh token obtained from login or registration
 */
@Schema(description = "Request to obtain a new access token using a refresh token")
public record RefreshTokenRequest(
        @Schema(
                description = "Long-lived refresh token used to obtain a new access token",
                example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjMiLCJ0eXBlIjoicmVmcmVzaCIsImlhdCI6MTcwMzI1NjAwMCwiZXhwIjoxNzA1ODQ4MDAwfQ.signature"
        )
        @NotBlank(message = "Refresh token is required")
        String refreshToken
) {
}
