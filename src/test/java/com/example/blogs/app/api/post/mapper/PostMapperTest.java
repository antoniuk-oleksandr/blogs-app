package com.example.blogs.app.api.post.mapper;

import com.example.blogs.app.api.comment.entity.CommentEntity;
import com.example.blogs.app.api.comment.fixture.CommentFixtures;
import com.example.blogs.app.api.file.entity.FileEntity;
import com.example.blogs.app.api.file.fixture.FileFixtures;
import com.example.blogs.app.api.post.dto.*;
import com.example.blogs.app.api.post.entity.PostEntity;
import com.example.blogs.app.api.post.fixture.PostFixtures;
import com.example.blogs.app.api.user.entity.UserEntity;
import com.example.blogs.app.api.user.fixture.UserFixtures;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

class PostMapperTest {

    private final PostMapper postMapper = new PostMapperImpl();

    @Test
    void toPostDTO_shouldMapPostFieldsCorrectly() {
        LocalDateTime now = LocalDateTime.now().withNano(0);
        UserEntity author = UserFixtures.user(1L, now);
        PostEntity post = PostFixtures.post(1L, now, author);
        String previewImageUrl = "previewImageUrl";

        PostDTO result = postMapper.toPostDTO(post, List.of(), previewImageUrl);

        assertThat(result.id()).isEqualTo(post.getId());
        assertThat(result.title()).isEqualTo(post.getTitle());
        assertThat(result.content()).isEqualTo(post.getContent());
        assertThat(result.slug()).isEqualTo(post.getSlug());
        assertThat(result.createdAt()).isEqualTo(post.getCreatedAt());
        assertThat(result.previewImageUrl()).isEqualTo(previewImageUrl);
    }

    @Test
    void toPostDTO_shouldMapAuthorCorrectly() {
        LocalDateTime now = LocalDateTime.now().withNano(0);
        UserEntity author = UserFixtures.user(1L, now);
        author.setUsername("username");
        author.setProfilePictureUrl("profilePictureUrl");
        PostEntity post = PostFixtures.post(1L, now, author);
        String previewImageUrl = "previewImageUrl";

        PostDTO result = postMapper.toPostDTO(post, List.of(), previewImageUrl);

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
        String previewImageUrl = "previewImageUrl";

        List<CommentEntity> comments = List.of(
                CommentFixtures.commentEntity(1L, now, author, post),
                CommentFixtures.commentEntity(2L, now.plusMinutes(5), author, post)
        );

        PostDTO result = postMapper.toPostDTO(post, comments, previewImageUrl);

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
        String previewImageUrl = "previewImageUrl";

        PostDTO result = postMapper.toPostDTO(post, List.of(), previewImageUrl);

        assertThat(result.comments()).isEmpty();
    }

    @Test
    void toPostDTO_shouldReturnNull_whenAllParametersAreNull() {
        PostDTO result = postMapper.toPostDTO(null, null, null);

        assertThat(result).isNull();
    }

    @Test
    void toPostDTO_shouldReturnPostWithNullComments_whenCommentsAreNull() {
        LocalDateTime now = LocalDateTime.now().withNano(0);
        UserEntity author = UserFixtures.user(1L, now);
        PostEntity post = PostFixtures.post(1L, now, author);
        String previewImageUrl = "previewImageUrl";

        PostDTO result = postMapper.toPostDTO(post, null, previewImageUrl);

        assertThat(result).isNotNull();
        assertThat(result.comments()).isNull();
    }

    @Test
    void toPostDTO_shouldReturnNullFields_whenPostEntityIsNull() {
        String previewImageUrl = "previewImageUrl";

        PostDTO result = postMapper.toPostDTO(null, List.of(), previewImageUrl);

        assertThat(result).isNotNull();
        assertThat(result.id()).isNull();
        assertThat(result.title()).isNull();
        assertThat(result.content()).isNull();
        assertThat(result.slug()).isNull();
        assertThat(result.createdAt()).isNull();
        assertThat(result.author()).isNull();
        assertThat(result.comments()).isEmpty();
        assertThat(result.previewImageUrl()).isEqualTo(previewImageUrl);
    }

    @Test
    void toPostDTO_shouldReturnPostDTO_whenPreviewImageUrlIsNull() {
        LocalDateTime now = LocalDateTime.now().withNano(0);
        UserEntity author = UserFixtures.user(1L, now);
        PostEntity post = PostFixtures.post(1L, now, author);

        PostDTO result = postMapper.toPostDTO(post, List.of(), null);

        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(post.getId());
        assertThat(result.title()).isEqualTo(post.getTitle());
        assertThat(result.content()).isEqualTo(post.getContent());
        assertThat(result.slug()).isEqualTo(post.getSlug());
        assertThat(result.createdAt()).isEqualTo(post.getCreatedAt());
        assertThat(result.previewImageUrl()).isNull();
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
    void toPostUserSummaryDTO_shouldReturnNull_whenPostIsNull() {
        PostUserSummaryDTO result = postMapper.toPostUserSummaryDTO(null);

        assertThat(result).isNull();
    }

    @Test
    void toPostCommentSummaryDTO_shouldMapCommentFieldsCorrectly() {
        LocalDateTime now = LocalDateTime.now().withNano(0);
        UserEntity author = UserFixtures.user(1L, now);
        PostEntity post = PostFixtures.post(1L, now, author);
        CommentEntity comment = CommentFixtures.commentEntity(5L, now, author, post);
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
        CommentEntity comment = CommentFixtures.commentEntity(10L, now, author, post);

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
        CommentEntity comment = CommentFixtures.commentEntity(1L, now, author, post);
        comment.setUpdatedAt(now);

        PostCommentSummaryDTO result = postMapper.toPostCommentSummaryDTO(comment);

        assertThat(result.edited()).isFalse();
    }

    @Test
    void toPostCommentSummaryDTO_shouldReturnNull_whenCommentIsNull() {
        PostCommentSummaryDTO result = postMapper.toPostCommentSummaryDTO(null);

        assertThat(result).isNull();
    }

    @Test
    void toPostEntity_shouldUpdatePostEntity_whenTitleIsNull() {
        LocalDateTime now = LocalDateTime.now().withNano(0);
        UserEntity author = UserFixtures.user(1L, now);
        PostEntity postEntity = PostFixtures.post(1L, now, author);

        PostUpdateRequestDTO updateRequestDTO = new PostUpdateRequestDTO(
                null,
                "Updated Description",
                "Updated Content"
        );

        PostEntity result = postMapper.toPostEntity(updateRequestDTO, postEntity);

        assertThat(result.getTitle()).isEqualTo("title");
        assertThat(result.getDescription()).isEqualTo("Updated Description");
        assertThat(result.getContent()).isEqualTo("Updated Content");
    }

    @Test
    void toPostEntity_shouldUpdatePostEntity_whenDescriptionIsNull() {
        LocalDateTime now = LocalDateTime.now().withNano(0);
        UserEntity author = UserFixtures.user(1L, now);
        PostEntity postEntity = PostFixtures.post(1L, now, author);

        PostUpdateRequestDTO updateRequestDTO = new PostUpdateRequestDTO(
                "Updated Title",
                null,
                "Updated Content"
        );

        PostEntity result = postMapper.toPostEntity(updateRequestDTO, postEntity);

        assertThat(result.getTitle()).isEqualTo("Updated Title");
        assertThat(result.getDescription()).isEqualTo("description");
        assertThat(result.getContent()).isEqualTo("Updated Content");
    }

    @Test
    void toPostEntity_shouldUpdatePostEntity_whenContentIsNull() {
        LocalDateTime now = LocalDateTime.now().withNano(0);
        UserEntity author = UserFixtures.user(1L, now);
        PostEntity postEntity = PostFixtures.post(1L, now, author);

        PostUpdateRequestDTO updateRequestDTO = new PostUpdateRequestDTO(
                "Updated Title",
                "Updated Description",
                null
        );

        PostEntity result = postMapper.toPostEntity(updateRequestDTO, postEntity);

        assertThat(result.getTitle()).isEqualTo("Updated Title");
        assertThat(result.getDescription()).isEqualTo("Updated Description");
        assertThat(result.getContent()).isEqualTo("content");
    }

    @Test
    void toPostEntity_shouldUpdatePostEntity_whenPreviewImageUrlIsNull() {
        LocalDateTime now = LocalDateTime.now().withNano(0);
        UserEntity author = UserFixtures.user(1L, now);
        PostEntity postEntity = PostFixtures.post(1L, now, author);

        PostUpdateRequestDTO updateRequestDTO = new PostUpdateRequestDTO(
                "Updated Title",
                "Updated Description",
                "Updated Content"
        );

        PostEntity result = postMapper.toPostEntity(updateRequestDTO, postEntity);

        assertThat(result.getTitle()).isEqualTo("Updated Title");
        assertThat(result.getDescription()).isEqualTo("Updated Description");
        assertThat(result.getContent()).isEqualTo("Updated Content");
    }

    @Test
    void toPostEntity_shouldReturnPostEntity_whenRequestDTOIsNull() {
        Long userId = 1L;
        Long postId = 1L;
        LocalDateTime now = LocalDateTime.now().withNano(0);
        UserEntity author = UserFixtures.user(userId, now);
        PostEntity postEntity = PostFixtures.post(postId, now, author);

        PostEntity result = postMapper.toPostEntity(null, postEntity);

        assertThat(result).isEqualTo(postEntity);
    }

    @Test
    void toPostEntity_shouldReturnNull_whenAllParametersAreNull() {
        PostEntity result = postMapper.toPostEntity(null, null, null, null);

        assertThat(result).isNull();
    }

    @Test
    void toPostEntity_shouldMapAllFieldsCorrectly() {
        Long userId = 1L;
        Long fileId = 1L;
        LocalDateTime now = LocalDateTime.now().withNano(0);
        UserEntity author = UserFixtures.user(userId, now);
        String slug = "unique-slug";
        PostCreateRequestDTO createRequestDTO = new PostCreateRequestDTO(
                "New Title",
                "New Description",
                "New Content"
        );
        FileEntity file = FileFixtures.file(fileId, now);

        PostEntity result = postMapper.toPostEntity(createRequestDTO, slug, file, author);

        assertThat(result.getTitle()).isEqualTo("New Title");
        assertThat(result.getDescription()).isEqualTo("New Description");
        assertThat(result.getContent()).isEqualTo("New Content");
        assertThat(result.getSlug()).isEqualTo(slug);
        assertThat(result.getAuthor()).isEqualTo(author);
        assertThat(result.getUpdatedAt()).isEqualTo(author.getUpdatedAt());
        assertThat(result.getFile()).isEqualTo(file);
    }

    @Test
    void toPostUpdateResponseDTO_shouldMapPostFieldsCorrectly() {
        LocalDateTime now = LocalDateTime.now().withNano(0);
        UserEntity author = UserFixtures.user(1L, now);
        PostEntity post = PostFixtures.post(1L, now, author);
        String previewImageUrl = "previewImageUrl";

        PostUpdateResponseDTO result = postMapper.toPostUpdateResponseDTO(post, previewImageUrl);

        assertThat(result.id()).isEqualTo(post.getId());
        assertThat(result.title()).isEqualTo(post.getTitle());
        assertThat(result.description()).isEqualTo(post.getDescription());
        assertThat(result.slug()).isEqualTo(post.getSlug());
        assertThat(result.content()).isEqualTo(post.getContent());
        assertThat(result.updatedAt()).isEqualTo(post.getUpdatedAt());
        assertThat(result.previewImageUrl()).isEqualTo(previewImageUrl);
    }

    @Test
    void toPostUpdateResponseDTO_shouldReturnNull_whenAllParametersAreNull() {
        PostUpdateResponseDTO result = postMapper.toPostUpdateResponseDTO(null, null);

        assertThat(result).isNull();
    }

    @Test
    void toPostUpdateResponseDTO_shouldReturnNullPreviewImageUrl_whenPreviewImageUrlIsNull() {
        LocalDateTime now = LocalDateTime.now().withNano(0);
        UserEntity author = UserFixtures.user(1L, now);
        PostEntity post = PostFixtures.post(1L, now, author);

        PostUpdateResponseDTO result = postMapper.toPostUpdateResponseDTO(post, null);

        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(post.getId());
        assertThat(result.title()).isEqualTo(post.getTitle());
        assertThat(result.description()).isEqualTo(post.getDescription());
        assertThat(result.slug()).isEqualTo(post.getSlug());
        assertThat(result.content()).isEqualTo(post.getContent());
        assertThat(result.updatedAt()).isEqualTo(post.getUpdatedAt());
        assertThat(result.previewImageUrl()).isNull();
    }

    @Test
    void toPostUpdateResponseDTO_shouldReturnNullFields_whenPostEntityIsNull() {
        String previewImageUrl = "previewImageUrl";

        PostUpdateResponseDTO result = postMapper.toPostUpdateResponseDTO(null, previewImageUrl);

        assertThat(result).isNotNull();
        assertThat(result.id()).isNull();
        assertThat(result.title()).isNull();
        assertThat(result.description()).isNull();
        assertThat(result.slug()).isNull();
        assertThat(result.content()).isNull();
        assertThat(result.updatedAt()).isNull();
        assertThat(result.previewImageUrl()).isEqualTo(previewImageUrl);
    }

    @Test
    void toPostCreateResponseDTO_shouldReturnMappedFieldsCorrectly() {
        LocalDateTime now = LocalDateTime.now().withNano(0);
        UserEntity author = UserFixtures.user(1L, now);
        PostEntity post = PostFixtures.post(1L, now, author);
        String previewImageUrl = "previewImageUrl";

        PostCreateResponseDTO result = postMapper.toPostCreateResponseDTO(post, previewImageUrl);

        assertThat(result.id()).isEqualTo(post.getId());
        assertThat(result.title()).isEqualTo(post.getTitle());
        assertThat(result.description()).isEqualTo(post.getDescription());
        assertThat(result.slug()).isEqualTo(post.getSlug());
        assertThat(result.content()).isEqualTo(post.getContent());
        assertThat(result.createdAt()).isEqualTo(post.getCreatedAt());
        assertThat(result.previewImageUrl()).isEqualTo(previewImageUrl);
    }

    @Test
    void toPostCreateResponseDTO_shouldReturnNull_whenAllParametersAreNull() {
        PostCreateResponseDTO result = postMapper.toPostCreateResponseDTO(null, null);

        assertThat(result).isNull();
    }

    @Test
    void toPostCreateResponseDTO_shouldReturnNullFields_whenPostEntityIsNull() {
        String previewImageUrl = "previewImageUrl";

        PostCreateResponseDTO result = postMapper.toPostCreateResponseDTO(null, previewImageUrl);

        assertThat(result).isNotNull();
        assertThat(result.id()).isNull();
        assertThat(result.title()).isNull();
        assertThat(result.description()).isNull();
        assertThat(result.slug()).isNull();
        assertThat(result.content()).isNull();
        assertThat(result.createdAt()).isNull();
        assertThat(result.previewImageUrl()).isEqualTo(previewImageUrl);
    }
}
