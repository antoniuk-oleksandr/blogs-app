package com.example.blogs.app.api.post.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Request DTO for creating a new post")
public record PostCreateRequestDTO(

        @NotBlank(message = "Title must not be blank")
        @Schema(description = "Title of the post", example = "My First Blog Post")
        String title,

        @NotBlank(message = "Description must not be blank")
        @Schema(description = "Short description of the post", example = "An introduction to my blog")
        String description,

        @NotBlank(message = "Content must not be blank")
        @Schema(description = "Content of the post", example = "This is the full content of my first blog post...")
        String content
) {
}
