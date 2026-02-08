package com.example.blogs.app.api.post.service;

import com.example.blogs.app.api.post.dto.PostUpdateRequestDTO;
import com.example.blogs.app.api.post.entity.PostEntity;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Updates post slugs by regenerating them from the updated title when necessary.
 */
@Service
@AllArgsConstructor
public class PostSlugUpdater {

    private final SlugService slugService;

    /**
     * Applies slug update to a post entity if the title is being changed.
     * Generates a new slug from the title when a non-blank title is provided.
     *
     * @param post the post entity to update
     * @param dto  the update request containing the new title
     */
    public void apply(PostEntity post, PostUpdateRequestDTO dto) {
        if (dto.title() != null && !dto.title().isBlank()) {
            post.setSlug(slugService.generate(dto.title()));
        }
    }
}