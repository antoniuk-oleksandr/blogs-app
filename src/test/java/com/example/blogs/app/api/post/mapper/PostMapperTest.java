package com.example.blogs.app.api.post.mapper;

import com.example.blogs.app.api.comment.entity.CommentEntity;
import com.example.blogs.app.api.comment.fixture.CommentFixtures;
import com.example.blogs.app.api.post.dto.PostCommentSummaryDTO;
import com.example.blogs.app.api.post.dto.PostDTO;
import com.example.blogs.app.api.post.dto.PostUserSummaryDTO;
import com.example.blogs.app.api.post.entity.PostEntity;
import com.example.blogs.app.api.post.fixture.PostFixtures;
import com.example.blogs.app.api.user.entity.UserEntity;
import com.example.blogs.app.api.user.fixture.UserFixtures;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

class PostMapperTest {

    private PostMapper postMapper = new PostMapperImpl();

    @Test
    void toPostDTO_shouldMapPostFieldsCorrectly() {
        LocalDateTime now = LocalDateTime.now().withNano(0);
        UserEntity author = UserFixtures.user(1L, now);
        PostEntity post = PostFixtures.post(1L, now, author);

        PostDTO result = postMapper.toPostDTO(post, List.of());

        assertThat(result.id()).isEqualTo(post.getId());
        assertThat(result.title()).isEqualTo(post.getTitle());
        assertThat(result.content()).isEqualTo(post.getContent());
        assertThat(result.slug()).isEqualTo(post.getSlug());
        assertThat(result.previewImageUrl()).isEqualTo(post.getPreviewImageUrl());
        assertThat(result.createdAt()).isEqualTo(post.getCreatedAt());
    }

    @Test
    void toPostDTO_shouldMapAuthorCorrectly() {
        LocalDateTime now = LocalDateTime.now().withNano(0);
        UserEntity author = UserFixtures.user(1L, now);
        author.setUsername("username");
        author.setProfilePictureUrl("profilePictureUrl");
        PostEntity post = PostFixtures.post(1L, now, author);

        PostDTO result = postMapper.toPostDTO(post, List.of());

        assertThat(result.author()).isNotNull();
        assertThat(result.author().id()).isEqualTo(author.getId());
        assertThat(result.author().username()).isEqualTo(author.getUsername());
        assertThat(result.author().profilePictureUrl()).isEqualTo(author.getProfilePictureUrl());
    }

    @Test
    void toPostDTO_shouldMapCommentsCorrectly() {
        LocalDateTime now = LocalDateTime.now().withNano(0);
        UserEntity author = UserFixtures.user(1L, now);
        PostEntity post = PostFixtures.post(1L, now, author);

        List<CommentEntity> comments = List.of(
                CommentFixtures.comment(1L, now, author, post),
                CommentFixtures.comment(2L, now.plusMinutes(5), author, post)
        );

        PostDTO result = postMapper.toPostDTO(post, comments);

        assertThat(result.comments()).hasSize(2);
        assertThat(result.comments().getFirst().id()).isEqualTo(comments.getFirst().getId());
        assertThat(result.comments().getFirst().content()).isEqualTo(comments.getFirst().getContent());
        assertThat(result.comments().getFirst().createdAt()).isEqualTo(comments.getFirst().getCreatedAt());
        assertThat(result.comments().getFirst().author()).isNotNull();
        assertThat(result.comments().get(1).id()).isEqualTo(comments.get(1).getId());
        assertThat(result.comments().get(1).createdAt()).isEqualTo(comments.get(1).getCreatedAt());
    }

    @Test
    void toPostDTO_shouldHandleEmptyCommentsList() {
        LocalDateTime now = LocalDateTime.now().withNano(0);
        UserEntity author = UserFixtures.user(1L, now);
        PostEntity post = PostFixtures.post(1L, now, author);

        PostDTO result = postMapper.toPostDTO(post, List.of());

        assertThat(result.comments()).isEmpty();
    }

    @Test
    void toPostUserSummaryDTO_shouldMapUserFieldsCorrectly() {
        LocalDateTime now = LocalDateTime.now().withNano(0);
        UserEntity user = UserFixtures.user(1L, now);
        user.setUsername("username");
        user.setProfilePictureUrl("profilePictureUrl");

        PostUserSummaryDTO result = postMapper.toPostUserSummaryDTO(user);

        assertThat(result.id()).isEqualTo(user.getId());
        assertThat(result.username()).isEqualTo(user.getUsername());
        assertThat(result.profilePictureUrl()).isEqualTo(user.getProfilePictureUrl());
    }

    @Test
    void toPostUserSummaryDTO_shouldHandleNullProfilePicture() {
        LocalDateTime now = LocalDateTime.now().withNano(0);
        UserEntity user = UserFixtures.user(1L, now);
        user.setProfilePictureUrl(null);

        PostUserSummaryDTO result = postMapper.toPostUserSummaryDTO(user);

        assertThat(result.profilePictureUrl()).isNull();
    }

    @Test
    void toPostCommentSummaryDTO_shouldMapCommentFieldsCorrectly() {
        LocalDateTime now = LocalDateTime.now().withNano(0);
        UserEntity author = UserFixtures.user(1L, now);
        PostEntity post = PostFixtures.post(1L, now, author);
        CommentEntity comment = CommentFixtures.comment(5L, now, author, post);
        comment.setContent("content");

        PostCommentSummaryDTO result = postMapper.toPostCommentSummaryDTO(comment);

        assertThat(result.id()).isEqualTo(comment.getId());
        assertThat(result.content()).isEqualTo(comment.getContent());
        assertThat(result.createdAt()).isEqualTo(comment.getCreatedAt());
    }

    @Test
    void toPostCommentSummaryDTO_shouldMapCommentAuthorCorrectly() {
        LocalDateTime now = LocalDateTime.now().withNano(0);
        UserEntity author = UserFixtures.user(3L, now);
        author.setUsername("username");
        author.setProfilePictureUrl("profilePictureUrl");
        PostEntity post = PostFixtures.post(1L, now, author);
        CommentEntity comment = CommentFixtures.comment(10L, now, author, post);

        PostCommentSummaryDTO result = postMapper.toPostCommentSummaryDTO(comment);

        assertThat(result.author()).isNotNull();
        assertThat(result.author().id()).isEqualTo(author.getId());
        assertThat(result.author().username()).isEqualTo(author.getUsername());
        assertThat(result.author().profilePictureUrl()).isEqualTo(author.getProfilePictureUrl());
    }

    @Test
    void toPostCommentSummaryDTO_shouldIndicateNotEditedWhenTimesMatch() {
        LocalDateTime now = LocalDateTime.now().withNano(0);
        UserEntity author = UserFixtures.user(1L, now);
        PostEntity post = PostFixtures.post(1L, now, author);
        CommentEntity comment = CommentFixtures.comment(1L, now, author, post);
        comment.setUpdatedAt(now);

        PostCommentSummaryDTO result = postMapper.toPostCommentSummaryDTO(comment);

        assertThat(result.edited()).isFalse();
    }
}
