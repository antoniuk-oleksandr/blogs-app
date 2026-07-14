package com.example.blogs.app.api.search.fixture;

import com.example.blogs.app.api.search.dto.SearchPost;
import com.example.blogs.app.api.search.dto.SearchPostAuthor;

import java.time.LocalDateTime;

/**
 * Test fixture factory for creating search-related entities and DTOs with predefined values.
 */
public class SearchFixtures {

    /**
     * Creates a {@link SearchPost} with predefined values for the given ID.
     *
     * @param id the post ID
     * @return configured search post DTO
     */
    public static SearchPost searchPost(Long id) {
        return new SearchPost(
                id,
                "title",
                "description",
                "content",
                "slug-" + id,
                LocalDateTime.now().withNano(0),
                10,
                5,
                "previewPictureUrl",
                searchPostAuthor(1L)
        );
    }

    /**
     * Creates a {@link SearchPostAuthor} with predefined values for the given ID.
     *
     * @param id the author ID
     * @return configured search post author DTO
     */
    public static SearchPostAuthor searchPostAuthor(Long id) {
        return new SearchPostAuthor(id, "username", "firstName", "surname", "profilePictureUrl");
    }
}
