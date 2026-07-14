package com.example.blogs.app.api.search.dto;

import java.time.LocalDateTime;

public record SearchPost(
        Long id,
        String title,
        String description,
        String content,
        String slug,
        LocalDateTime createdAt,
        int likesCount,
        int commentsCount,
        String previewPictureUrl,
        SearchPostAuthor author
) {
}
