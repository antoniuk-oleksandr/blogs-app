package com.example.blogs.app.api.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

/**
 * Response payload containing updated user profile information.
 */
@Builder
public record UpdateUserResponseDTO(
        @Schema(description = "Updated username", example = "johndoe")
        String username,

        @Schema(description = "Updated user biography", example = "Software developer")
        String bio,

        @Schema(description = "URL of the user's profile picture", example = "https://example.com/profiles/user123.jpg")
        String profilePictureUrl
) {
}
