package com.example.blogs.app.api.post.dto;

import com.example.blogs.app.validation.AtLeastOneField;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Request DTO for updating a post with partial field updates.
 */
@AtLeastOneField(
        fields = {"title", "description", "content", "previewImageUrl"}
)
@Schema(description = "Request DTO for updating a post")
public record PostUpdateRequestDTO(
        @Schema(description = "Title of the post", example = "Updated Post Title")
        String title,

        @Schema(description = "Description of the post", example = "This is an updated description of the post.")
        String description,

        @Schema(description = "Content of the post", example = "This is the updated content of the post.")
        String content,

        @Schema(description = "Preview image URL of the post", example = "http://example.com/updated-image.jpg")
        String previewImageUrl
) {
}
