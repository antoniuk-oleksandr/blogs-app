package com.example.blogs.app.api.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

/**
 * Request payload for user authentication.
 */
public record LoginRequest(
        @Schema(description = "Username or email address")
        @NotBlank(message = "Username or email is required")
        String usernameOrEmail,

        @Schema(description = "User password")
        @NotBlank(message = "Password is required")
        String password
) {
}
