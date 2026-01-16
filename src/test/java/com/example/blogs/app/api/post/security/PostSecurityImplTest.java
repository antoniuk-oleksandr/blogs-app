package com.example.blogs.app.api.post.security;

import com.example.blogs.app.api.post.exception.PostNotFoundException;
import com.example.blogs.app.api.post.repository.adapter.PostRepositoryAdapter;
import com.example.blogs.app.security.UserPrincipal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PostSecurityImplTest {

    @Mock
    private PostRepositoryAdapter postRepositoryAdapter;

    @Mock
    private Authentication authentication;

    private PostSecurity postSecurity;

    @BeforeEach
    void setUp() {
        postSecurity = new PostSecurityImpl(postRepositoryAdapter);
    }

    @Test
    void isOwner_shouldReturnTrue_whenUserIsOwner() {
        when(postRepositoryAdapter.existsById(anyLong()))
                .thenReturn(true);
        when(postRepositoryAdapter.existsByIdAndAuthorId(anyLong(), anyLong()))
                .thenReturn(true);

        UserPrincipal principal = new UserPrincipal(
                1L, "username", "email", "pfp"
        );
        when(authentication.getPrincipal()).thenReturn(principal);

        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authentication);
        SecurityContextHolder.setContext(context);

        boolean result = postSecurity.isOwner(1L);
        assertThat(result).isTrue();

        verify(postRepositoryAdapter, times(1)).existsById(1L);
        verify(postRepositoryAdapter, times(1)).existsByIdAndAuthorId(1L, 1L);
    }

    @Test
    void isOwner_shouldThrowPostNotFoundException_whenPostDoesNotExist() {
        when(postRepositoryAdapter.existsById(anyLong()))
                .thenReturn(false);

        assertThatThrownBy(() -> postSecurity.isOwner(1L))
                .isInstanceOf(PostNotFoundException.class)
                .hasMessage("Post not found");

        verify(postRepositoryAdapter, times(1)).existsById(1L);
        verify(postRepositoryAdapter, never()).existsByIdAndAuthorId(anyLong(), anyLong());
    }
}
