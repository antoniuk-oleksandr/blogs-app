package com.example.blogs.app.api.post.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
@Schema(description = "Response DTO for updating a post")
public record PostUpdateResponseDTO(
        Long id,
        String title,
        String description,
        String content,
        String slug,
        String previewImageUrl,
        LocalDateTime updatedAt
) {
}
