package com.example.blogs.app.api.comment.security;

import com.example.blogs.app.api.auth.fixture.AuthFixtures;
import com.example.blogs.app.api.comment.exception.CommentNotFoundException;
import com.example.blogs.app.api.comment.repository.adapter.CommentRepositoryAdapter;
import com.example.blogs.app.security.UserPrincipal;
import com.example.blogs.app.security.UserPrincipalAuthenticationToken;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CommentSecurityTest {

    @Mock
    private CommentRepositoryAdapter commentRepositoryAdapter;

    private CommentSecurity commentSecurity;

    @BeforeEach
    void setUp() {
        commentSecurity = new CommentSecurity(commentRepositoryAdapter);
    }

    @Test
    void isOwner_ShouldReturnTrue_WhenUserIsOwner() {
        Long commentId = 1L;

        Jwt jwt = AuthFixtures.jwt();
        UserPrincipal userPrincipal = AuthFixtures.userPrincipal();
        Authentication auth = new UserPrincipalAuthenticationToken(userPrincipal, jwt);
        SecurityContextHolder.getContext().setAuthentication(auth);

        when(commentRepositoryAdapter.existsById(anyLong())).thenReturn(true);
        when(commentRepositoryAdapter.existsByIdAndAuthorId(anyLong(), anyLong())).thenReturn(true);

        boolean result = commentSecurity.isOwner(commentId);

        assertThat(result).isTrue();
        verify(commentRepositoryAdapter).existsById(commentId);
        verify(commentRepositoryAdapter).existsByIdAndAuthorId(commentId, userPrincipal.id());
    }

    @Test
    void isOwner_shouldThrowCommentNotFoundException_WhenCommentDoesNotExist() {
        Long commentId = 1L;

        when(commentRepositoryAdapter.existsById(anyLong())).thenReturn(false);

        assertThatThrownBy(() -> commentSecurity.isOwner(commentId))
                .isInstanceOf(CommentNotFoundException.class)
                .hasMessage("Comment not found");

        verify(commentRepositoryAdapter).existsById(commentId);
        verify(commentRepositoryAdapter, never()).existsByIdAndAuthorId(anyLong(), anyLong());
    }
}
