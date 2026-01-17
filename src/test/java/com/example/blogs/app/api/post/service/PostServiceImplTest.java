package com.example.blogs.app.api.post.service;

import com.example.blogs.app.api.comment.entity.CommentEntity;
import com.example.blogs.app.api.comment.fixture.CommentFixtures;
import com.example.blogs.app.api.comment.service.CommentService;
import com.example.blogs.app.api.post.dto.*;
import com.example.blogs.app.api.post.entity.PostEntity;
import com.example.blogs.app.api.post.fixture.PostFixtures;
import com.example.blogs.app.api.post.mapper.PostMapper;
import com.example.blogs.app.api.post.repository.adapter.PostRepositoryAdapter;
import com.example.blogs.app.api.user.entity.UserEntity;
import com.example.blogs.app.api.user.fixture.UserFixtures;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class PostServiceImplTest {

    @Mock
    private PostRepositoryAdapter postRepositoryAdapter;

    @Mock
    private CommentService commentService;

    @Mock
    private PostMapper postMapper;

    @Mock
    private SlugService slugService;

    private PostService postService;

    @BeforeEach
    void setUp() {
        postService = new PostServiceImpl(postRepositoryAdapter, commentService, postMapper, slugService);
    }

    @Test
    void getPostsByUserId_shouldReturnPostsForGivenUserId() {
        UserEntity mockUser = UserEntity.builder().id(1L).username("testuser").build();
        List<PostEntity> mockPosts = List.of(
                PostEntity.builder()
                        .id(1L).author(mockUser).title("Post 1").content("Content 1").build(),
                PostEntity.builder()
                        .id(2L).author(mockUser).title("Post 2").content("Content 2").build()
        );

        when(postRepositoryAdapter.findByAuthorId(anyLong())).thenReturn(mockPosts);

        List<PostEntity> result = postService.getPostsByUserId(1L);
        assertThat(result).isEqualTo(mockPosts);
        verify(postRepositoryAdapter, times(1)).findByAuthorId(1L);
    }

    @Test
    void deletePostById_shouldInvokeRepositoryDelete() {
        Long postId = 1L;

        postService.deletePostById(postId);

        verify(postRepositoryAdapter).deleteById(postId);
    }

    @Test
    void getPostBySlug_shouldReturnPostDTO() {
        LocalDateTime now = LocalDateTime.now().withNano(0);
        UserEntity author = UserFixtures.user(1L, now);
        PostEntity post = PostFixtures.post(1L, now, author);
        List<CommentEntity> comments = List.of(
                CommentFixtures.comment(1L, now, author, post),
                CommentFixtures.comment(2L, now, author, post)
        );
        PostUserSummaryDTO authorDTO = PostFixtures.postUserSummaryDTO(author.getId());
        List<PostCommentSummaryDTO> commentDTOs = List.of(
                PostFixtures.postCommentSummaryDTO(1L, now, authorDTO),
                PostFixtures.postCommentSummaryDTO(2L, now, authorDTO)
        );
        PostDTO expectedDTO = PostFixtures.postDTO(1L, now, authorDTO, commentDTOs);
        when(postRepositoryAdapter.findBySlug(anyString())).thenReturn(post);
        when(commentService.getCommentsByPostId(post.getId())).thenReturn(comments);
        when(postMapper.toPostDTO(post, comments)).thenReturn(expectedDTO);

        PostDTO result = postService.getPostBySlug(post.getSlug());

        assertThat(result)
                .isNotNull()
                .isEqualTo(expectedDTO)
                .satisfies(dto -> {
                    assertThat(dto.id()).isEqualTo(1L);
                    assertThat(dto.slug()).isEqualTo(post.getSlug());
                });
        verify(postRepositoryAdapter).findBySlug(post.getSlug());
        verify(commentService).getCommentsByPostId(1L);
        verify(postMapper).toPostDTO(post, comments);
        verifyNoMoreInteractions(postRepositoryAdapter, commentService, postMapper);
    }

    @Test
    void updatePostById_shouldReturnPostUpdateResponseDTO_whenTitleIsNull() {
        LocalDateTime now = LocalDateTime.now().withNano(0);
        UserEntity author = UserFixtures.user(1L, now);
        PostEntity existingPost = PostFixtures.post(1L, now, author);

        PostUpdateRequestDTO request = new PostUpdateRequestDTO(
                null,
                "Updated Description",
                "Updated Content",
                "http://example.com/updated-image.jpg"
        );

        PostEntity updatedPost = PostEntity.builder()
                .id(1L)
                .author(author)
                .title(existingPost.getTitle()) // Title unchanged
                .description(request.description())
                .content(request.content())
                .previewImageUrl(request.previewImageUrl())
                .slug(existingPost.getSlug())
                .createdAt(existingPost.getCreatedAt())
                .build();

        PostUpdateResponseDTO expectedResponse = new PostUpdateResponseDTO(
                1L,
                existingPost.getTitle(),
                request.description(),
                request.content(),
                existingPost.getSlug(),
                request.previewImageUrl(),
                now
        );

        when(postRepositoryAdapter.findById(1L)).thenReturn(existingPost);
        when(postMapper.toPostEntity(request, existingPost)).thenReturn(updatedPost);
        when(postRepositoryAdapter.update(updatedPost)).thenReturn(updatedPost);
        when(postMapper.toPostUpdateResponseDTO(updatedPost)).thenReturn(expectedResponse);

        PostUpdateResponseDTO response = postService.updatePostById(1L, request);

        assertThat(response).isEqualTo(expectedResponse);
        verify(postRepositoryAdapter).findById(1L);
        verify(postMapper).toPostEntity(request, existingPost);
        verify(postRepositoryAdapter).update(updatedPost);
        verify(postMapper).toPostUpdateResponseDTO(updatedPost);
    }

    @Test
    void updatePostById_shouldReturnPostUpdateResponseDTO_whenTitleIsBlank() {
        LocalDateTime now = LocalDateTime.now().withNano(0);
        UserEntity author = UserFixtures.user(1L, now);
        PostEntity existingPost = PostFixtures.post(1L, now, author);

        PostUpdateRequestDTO request = new PostUpdateRequestDTO(
                "",
                "Updated Description",
                "Updated Content",
                "http://example.com/updated-image.jpg"
        );

        PostEntity updatedPost = PostEntity.builder()
                .id(1L)
                .author(author)
                .title(existingPost.getTitle())
                .description(request.description())
                .content(request.content())
                .previewImageUrl(request.previewImageUrl())
                .slug(existingPost.getSlug())
                .createdAt(existingPost.getCreatedAt())
                .build();

        PostUpdateResponseDTO expectedResponse = new PostUpdateResponseDTO(
                1L,
                existingPost.getTitle(),
                request.description(),
                request.content(),
                existingPost.getSlug(),
                request.previewImageUrl(),
                now
        );

        when(postRepositoryAdapter.findById(1L)).thenReturn(existingPost);
        when(postMapper.toPostEntity(request, existingPost)).thenReturn(updatedPost);
        when(postRepositoryAdapter.update(updatedPost)).thenReturn(updatedPost);
        when(postMapper.toPostUpdateResponseDTO(updatedPost)).thenReturn(expectedResponse);

        PostUpdateResponseDTO response = postService.updatePostById(1L, request);

        assertThat(response).isEqualTo(expectedResponse);
        verify(postRepositoryAdapter).findById(1L);
        verify(postMapper).toPostEntity(request, existingPost);
        verify(postRepositoryAdapter).update(updatedPost);
        verify(postMapper).toPostUpdateResponseDTO(updatedPost);
    }

    @Test
    void updatePostById_shouldGenerateSlugAndReturnPostUpdateResponseDTO_whenTitleIsProvided() {
        LocalDateTime now = LocalDateTime.now().withNano(0);
        UserEntity author = UserFixtures.user(1L, now);
        PostEntity existingPost = PostFixtures.post(1L, now, author);

        PostUpdateRequestDTO request = new PostUpdateRequestDTO(
                "New Title",
                "Updated Description",
                "Updated Content",
                "http://example.com/updated-image.jpg"
        );

        String generatedSlug = "new-title-slug";

        PostEntity updatedPost = PostEntity.builder()
                .id(1L)
                .author(author)
                .title(request.title())
                .description(request.description())
                .content(request.content())
                .previewImageUrl(request.previewImageUrl())
                .slug(generatedSlug)
                .createdAt(existingPost.getCreatedAt())
                .build();

        PostUpdateResponseDTO expectedResponse = new PostUpdateResponseDTO(
                1L,
                request.title(),
                request.description(),
                request.content(),
                generatedSlug,
                request.previewImageUrl(),
                now
        );

        when(postRepositoryAdapter.findById(1L)).thenReturn(existingPost);
        when(slugService.generate(request.title())).thenReturn(generatedSlug);
        when(postMapper.toPostEntity(request, existingPost)).thenReturn(updatedPost);
        when(postRepositoryAdapter.update(updatedPost)).thenReturn(updatedPost);
        when(postMapper.toPostUpdateResponseDTO(updatedPost)).thenReturn(expectedResponse);

        PostUpdateResponseDTO response = postService.updatePostById(1L, request);

        assertThat(response).isEqualTo(expectedResponse);
        verify(postRepositoryAdapter).findById(1L);
        verify(slugService).generate(request.title());
        verify(postMapper).toPostEntity(request, existingPost);
        verify(postRepositoryAdapter).update(updatedPost);
        verify(postMapper).toPostUpdateResponseDTO(updatedPost);
    }
}
