package com.example.blogs.app.api.post.service;

import com.example.blogs.app.api.post.entity.PostEntity;
import com.example.blogs.app.api.post.repository.adapter.PostRepositoryAdapter;
import com.example.blogs.app.api.user.entity.UserEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class PostServiceImplTest {

    @Mock
    private PostRepositoryAdapter postRepositoryAdapter;

    private PostService postService;

    @BeforeEach
    void setUp() {
        postService = new PostServiceImpl(postRepositoryAdapter);
    }

    @Test
    void getPostsByUserId_shouldReturnPostsForGivenUserId() {
        UserEntity mockUser = UserEntity.builder().id(1L).username("testuser").build();
        List<PostEntity> mockPosts = List.of(
                PostEntity.builder().id(1L).author(mockUser).title("Post 1").content("Content 1").build(),
                PostEntity.builder().id(2L).author(mockUser).title("Post 2").content("Content 2").build()
        );

        when(postRepositoryAdapter.findByAuthorId(anyLong())).thenReturn(mockPosts);

        List<PostEntity> result = postService.getPostsByUserId(1L);
        assertThat(result).isEqualTo(mockPosts);
        verify(postRepositoryAdapter, times(1)).findByAuthorId(1L);
    }
}
