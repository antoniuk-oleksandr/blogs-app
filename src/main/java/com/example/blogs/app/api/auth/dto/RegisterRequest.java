package com.example.blogs.app.api.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Request payload for user registration containing credentials and profile information.
 *
 * @param username unique username (3-16 characters)
 * @param password account password (minimum 8 characters)
 * @param email valid and unique email address
 */
@Schema(description = "User registration request payload")
public record RegisterRequest(
        @Schema(description = "Unique username (3-16 alphanumeric characters)", example = "johndoe")
        @Size(min = 3, max = 16, message = "Username must be between 3 and 16 characters")
        @NotBlank(message = "Username is required")
        String username,

        @Schema(description = "Account password (minimum 8 characters)", example = "SecurePass123!", format = "password")
        @Size(min = 8, message = "Password must be at least 8 characters long")
        @NotBlank(message = "Password is required")
        String password,

        @Schema(description = "Valid email address (must be unique)", example = "john.doe@example.com")
        @Email(message = "Email should be valid")
        @NotBlank(message = "Email is required")
        String email
) {
}
