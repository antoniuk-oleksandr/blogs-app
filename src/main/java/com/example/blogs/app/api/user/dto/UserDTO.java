package com.example.blogs.app.api.user.dto;

import lombok.Builder;

import java.util.List;

/**
 * User profile data transfer object containing personal information and post summaries.
 */
@Builder
public record UserDTO(
        String username,
        String bio,
        String profilePictureUrl,
        List<UserPostSummaryDTO> posts
) {
}
