package com.example.blogs.app.api.post.fixture;

import com.example.blogs.app.api.post.dto.PostCommentSummaryDTO;
import com.example.blogs.app.api.post.dto.PostDTO;
import com.example.blogs.app.api.post.dto.PostUserSummaryDTO;
import com.example.blogs.app.api.post.entity.PostEntity;
import com.example.blogs.app.api.user.entity.UserEntity;
import com.example.blogs.app.api.user.fixture.UserFixtures;

import java.time.LocalDateTime;
import java.util.List;

public class PostFixtures {
    public static PostEntity post() {
        LocalDateTime now = LocalDateTime.now().withNano(0);
        return post(null, now, UserFixtures.user());
    }

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

    public static PostUserSummaryDTO postUserSummaryDTO(Long id) {
        return PostUserSummaryDTO.builder()
                .id(id)
                .username("username")
                .profilePictureUrl("profilePictureUrl")
                .build();
    }

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

    public static PostDTO postDTO(Long id, LocalDateTime time) {
        return postDTO(id, time, postUserSummaryDTO(1L), List.of());
    }
}
