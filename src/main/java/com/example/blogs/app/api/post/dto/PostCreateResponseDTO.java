package com.example.blogs.app.api.post.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.time.LocalDateTime;

/**
 * Response data transfer object containing created post details with ID, slug, and preview image URL.
 */
@Builder
@Schema(description = "Response DTO for created post")
public record PostCreateResponseDTO(
        @Schema(description = "Unique identifier of the post", example = "1")
        Long id,

        @Schema(description = "Title of the post", example = "My First Blog Post")
        String title,

        @Schema(description = "Short description of the post", example = "This is a brief summary of my first blog post.")
        String description,

        @Schema(description = "Content of the post", example = "This is the full content of my first blog post...")
        String content,

        @Schema(description = "Unique slug for the post", example = "my-first-blog-post")
        String slug,

        @Schema(description = "URL of the preview image for the post", example = "http://example.com/images/preview.jpg")
        String previewImageUrl,

        @Schema(description = "Timestamp when the post was created", example = "2026-01-16T16:16:26")
        LocalDateTime createdAt
) {
}
