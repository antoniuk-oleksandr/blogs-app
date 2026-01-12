package com.example.blogs.app.api.user.dto;

import lombok.Builder;

import java.time.LocalDateTime;

/**
 * Summary information for a user's post including title, description, and preview details.
 */
@Builder
public record UserPostSummaryDto(
        String title,
        String description,
        String slug,
        LocalDateTime createdAt,
        String previewImageUrl
) {
}
