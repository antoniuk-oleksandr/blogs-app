package com.example.blogs.app.api.post.fixture;

import com.example.blogs.app.api.post.dto.PostCommentSummaryDTO;
import com.example.blogs.app.api.post.dto.PostDTO;
import com.example.blogs.app.api.post.dto.PostUserSummaryDTO;
import com.example.blogs.app.api.post.entity.PostEntity;
import com.example.blogs.app.api.user.entity.UserEntity;
import com.example.blogs.app.api.user.fixture.UserFixtures;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Test fixture factory for creating post entities and DTOs with predefined values.
 */
public class PostFixtures {

    /**
     * Creates a post entity with default timestamp and user.
     *
     * @return configured post entity
     */
    public static PostEntity post() {
        LocalDateTime now = LocalDateTime.now().withNano(0);
        return post(null, now, UserFixtures.user());
    }

    /**
     * Creates a post entity with the specified attributes.
     *
     * @param id the post ID
     * @param time the creation and update timestamp
     * @param author the post author
     * @return configured post entity
     */
    public static PostEntity post(Long id, LocalDateTime time, UserEntity author) {
        return PostEntity.builder()
                .id(id)
                .title("title")
                .description("description")
                .slug("slug")
                .previewImageUrl("previewImageUrl")
                .content("content")
                .createdAt(time)
                .updatedAt(time)
                .author(author)
                .build();
    }

    /**
     * Creates a post entity with the specified author.
     *
     * @param author the post author
     * @return configured post entity
     */
    public static PostEntity post(UserEntity author) {
        return PostEntity.builder()
                .title("title")
                .description("description")
                .previewImageUrl("previewImageUrl")
                .slug("slug")
                .content("content")
                .author(author)
                .build();
    }

    /**
     * Creates a post user summary DTO with the specified ID.
     *
     * @param id the user ID
     * @return configured post user summary DTO
     */
    public static PostUserSummaryDTO postUserSummaryDTO(Long id) {
        return PostUserSummaryDTO.builder()
                .id(id)
                .username("username")
                .profilePictureUrl("profilePictureUrl")
                .build();
    }

    /**
     * Creates a post comment summary DTO with the specified attributes.
     *
     * @param id the comment ID
     * @param time the creation timestamp
     * @param author the comment author
     * @return configured post comment summary DTO
     */
    public static PostCommentSummaryDTO postCommentSummaryDTO(
            Long id, LocalDateTime time, PostUserSummaryDTO author
    ) {
        return PostCommentSummaryDTO.builder()
                .id(id)
                .content("content")
                .author(author)
                .createdAt(time)
                .edited(false)
                .build();
    }

    /**
     * Creates a post DTO with the specified attributes.
     *
     * @param id the post ID
     * @param time the creation timestamp
     * @param author the post author
     * @param comments the list of comment summaries
     * @return configured post DTO
     */
    public static PostDTO postDTO(
            Long id, LocalDateTime time, PostUserSummaryDTO author, List<PostCommentSummaryDTO> comments
    ) {
        return PostDTO.builder()
                .id(id)
                .title("title")
                .slug("slug")
                .content("content")
                .previewImageUrl("previewImageUrl")
                .createdAt(time)
                .author(author)
                .comments(comments)
                .build();
    }

    /**
     * Creates a post DTO with the specified ID and timestamp using default author and empty comments.
     *
     * @param id the post ID
     * @param time the creation timestamp
     * @return configured post DTO
     */
    public static PostDTO postDTO(Long id, LocalDateTime time) {
        return postDTO(id, time, postUserSummaryDTO(1L), List.of());
    }
}
