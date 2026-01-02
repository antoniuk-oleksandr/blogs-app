package com.example.blogs.app.security;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Represents the authenticated user's identity extracted from JWT token claims.
 */
@Schema(description = "Authenticated user principal information")
public record UserPrincipal(
        @Schema(description = "Unique user identifier", example = "1")
        Long id,

        @Schema(description = "User's username", example = "johndoe")
        String username,

        @Schema(description = "User's email address", example = "johndoe@example.com")
        String email,

        @Schema(description = "URL to user's profile picture", example = "https://example.com/profile.jpg")
        String profilePictureUrl
){
}
