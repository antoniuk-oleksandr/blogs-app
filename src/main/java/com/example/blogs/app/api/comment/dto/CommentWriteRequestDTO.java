package com.example.blogs.app.api.comment.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

/**
 * Data transfer object for creating or updating a comment.
 */
public record CommentWriteRequestDTO(

        @NotBlank(message = "Content must not be blank")
        @Schema(description = "The content of the comment", example = "This is a comment.")
        String content
) {
}
