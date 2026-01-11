package com.example.blogs.app.api.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

/**
 * Request payload for user logout containing the refresh token to revoke.
 *
 * @param refreshToken JWT refresh token to be invalidated
 */
@Schema(description = "User logout request payload")
public record LogoutRequest(
        @Schema(
                description = "Refresh token to be revoked and invalidated",
                example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
        )
        @NotBlank(message = "Refresh token is required")
        String refreshToken
) {
}
