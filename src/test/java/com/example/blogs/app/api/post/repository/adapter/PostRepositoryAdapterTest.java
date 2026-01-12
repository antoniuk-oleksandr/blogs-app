package com.example.blogs.app.api.post.repository.adapter;

import com.example.blogs.app.api.post.entity.PostEntity;
import com.example.blogs.app.api.post.exception.FailedToFindPostsByAuthorIdException;
import com.example.blogs.app.api.post.repository.PostRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PostRepositoryAdapterTest {

    @Mock
    private PostRepository postRepository;

    private PostRepositoryAdapter postRepositoryAdapter;

    @BeforeEach
    void setUp() {
        postRepositoryAdapter = new PostRepositoryAdapterImpl(postRepository);
    }

    @Test
    void findByAuthorId_shouldReturnPosts_whenAuthorIdExists() {
        List<PostEntity> mockPosts = List.of(new PostEntity(), new PostEntity());
        when(postRepository.findByAuthorId(anyLong())).thenReturn(mockPosts);

        List<PostEntity> result = postRepositoryAdapter.findByAuthorId(1L);

        assertThat(result).isEqualTo(mockPosts);
        verify(postRepository).findByAuthorId(1L);
    }

    @Test
    void findByAuthorId_shouldThrowException_whenRepositoryFails() {
        when(postRepository.findByAuthorId(anyLong())).thenThrow(FailedToFindPostsByAuthorIdException.class);

        assertThatThrownBy(() -> postRepositoryAdapter.findByAuthorId(1L))
                .isInstanceOf(RuntimeException.class);
        verify(postRepository).findByAuthorId(1L);
    }
}
