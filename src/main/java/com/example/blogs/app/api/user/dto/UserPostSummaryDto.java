package com.example.blogs.app.api.user.dto;

import java.time.LocalDateTime;

public record UserPostSummaryDto(
        String title,
        String description,
        String slug,
        LocalDateTime createdAt,
        String previewImageUrl
) {
}
