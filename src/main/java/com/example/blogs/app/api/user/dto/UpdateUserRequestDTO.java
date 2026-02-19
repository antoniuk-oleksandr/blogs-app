package com.example.blogs.app.api.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Request payload for updating user profile information.
 * All fields are optional - only provided fields will be updated.
 */
public record UpdateUserRequestDTO(
        @Schema(description = "New email address", example = "newemail@example.com")
        String email,

        @Schema(description = "User biography/description", example = "Software developer passionate about clean code")
        String bio,

        @Schema(description = "New password (will be hashed before storage)", example = "newSecurePassword123")
        String password,

        @Schema(description = "New username (must be unique)", example = "newusername")
        String username
) {
}
