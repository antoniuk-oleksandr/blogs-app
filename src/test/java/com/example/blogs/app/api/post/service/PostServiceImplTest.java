package com.example.blogs.app.api.post.service;

import com.example.blogs.app.api.comment.entity.CommentEntity;
import com.example.blogs.app.api.comment.fixture.CommentFixtures;
import com.example.blogs.app.api.comment.service.CommentService;
import com.example.blogs.app.api.post.dto.PostCommentSummaryDTO;
import com.example.blogs.app.api.post.dto.PostDTO;
import com.example.blogs.app.api.post.dto.PostUserSummaryDTO;
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

    private PostService postService;

    @BeforeEach
    void setUp() {
        postService = new PostServiceImpl(postRepositoryAdapter, commentService, postMapper);
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
}
