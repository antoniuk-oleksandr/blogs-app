package com.example.blogs.app.api.search.dto;

import java.util.List;

public record SearchPostsResponseDTO(
        List<SearchPost> posts,
        String nextCursor,
        boolean hasMore
) {
}
