package com.example.blogs.app.api.post.service;

import com.example.blogs.app.api.post.dto.PostUpdateRequestDTO;
import com.example.blogs.app.api.post.entity.PostEntity;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class PostSlugUpdater {

    private final SlugService slugService;

    public void apply(PostEntity post, PostUpdateRequestDTO dto) {
        if (dto.title() != null && !dto.title().isBlank()) {
            post.setSlug(slugService.generate(dto.title()));
        }
    }
}