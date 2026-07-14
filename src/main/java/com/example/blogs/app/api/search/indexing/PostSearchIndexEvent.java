package com.example.blogs.app.api.search.indexing;

public record PostSearchIndexEvent(
        PostSearchIndexAction action,
        Long postId
) {
}
