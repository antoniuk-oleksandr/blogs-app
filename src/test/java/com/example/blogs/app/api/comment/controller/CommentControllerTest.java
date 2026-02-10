package com.example.blogs.app.api.comment.controller;

import com.example.blogs.app.api.auth.fixture.AuthFixtures;
import com.example.blogs.app.api.comment.dto.CommentCreateRequestDTO;
import com.example.blogs.app.api.comment.dto.CommentDTO;
import com.example.blogs.app.api.comment.exception.FailedToDeleteCommentException;
import com.example.blogs.app.api.comment.fixture.CommentFixtures;
import com.example.blogs.app.api.comment.service.CommentService;
import com.example.blogs.app.exception.ErrorResponseWriter;
import com.example.blogs.app.exception.ExceptionHttpStatusMapper;
import com.example.blogs.app.exception.GlobalExceptionHandler;
import com.example.blogs.app.security.UserPrincipal;
import com.example.blogs.app.security.UserPrincipalAuthenticationToken;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc(addFilters = false)
@WebMvcTest(controllers = CommentController.class)
@Import({
        GlobalExceptionHandler.class,
        ExceptionHttpStatusMapper.class,
        ErrorResponseWriter.class
})
class CommentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private CommentService commentService;

    @Test
    @SneakyThrows
    void createComment_shouldCreateCommentSuccessfully() {
        Jwt jwt = AuthFixtures.jwt();
        UserPrincipal userPrincipal = AuthFixtures.userPrincipal();
        Authentication auth = new UserPrincipalAuthenticationToken(userPrincipal, jwt);
        SecurityContextHolder.getContext().setAuthentication(auth);

        Long commentId = 1L;
        Long postId = 1L;
        Long authorId = 1L;
        String content = "content";
        boolean edited = false;
        LocalDateTime now = LocalDateTime.now();
        String nowStr = objectMapper.writeValueAsString(now).replace("\"", "");
        CommentDTO mockCommentDTO = CommentFixtures.commentDTO(commentId, postId, authorId, now);
        when(commentService.createComment(anyLong(), anyLong(), any(CommentCreateRequestDTO.class)))
                .thenReturn(mockCommentDTO);

        try {
            mockMvc.perform(post("/posts/{postId}/comment", postId)
                            .contentType("application/json")
                            .content("""
                                    {
                                        "content": "content"
                                    }
                                    """))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.id").value(commentId))
                    .andExpect(jsonPath("$.postId").value(postId))
                    .andExpect(jsonPath("$.authorId").value(authorId))
                    .andExpect(jsonPath("$.content").value(content))
                    .andExpect(jsonPath("$.createdAt").value(nowStr))
                    .andExpect(jsonPath("$.updatedAt").value(nowStr))
                    .andExpect(jsonPath("$.edited").value(edited));

            verify(commentService).createComment(eq(authorId), eq(postId), any(CommentCreateRequestDTO.class));
        } finally {
            SecurityContextHolder.clearContext();
        }
    }

    @Test
    @SneakyThrows
    void createComment_shouldReturn400_whenRequestBodyIsNull() {
        Jwt jwt = AuthFixtures.jwt();
        UserPrincipal userPrincipal = AuthFixtures.userPrincipal();
        Authentication auth = new UserPrincipalAuthenticationToken(userPrincipal, jwt);
        SecurityContextHolder.getContext().setAuthentication(auth);

        Long postId = 1L;

        try {
            mockMvc.perform(post("/posts/{postId}/comment", postId)
                            .contentType("application/json"))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message").value("Validation Failed"))
                    .andExpect(jsonPath("$.errors[0]").value("Request body is required"));

            verify(commentService, never()).createComment(anyLong(), anyLong(), any());
        } finally {
            SecurityContextHolder.clearContext();
        }
    }


    @ParameterizedTest
    @CsvSource({
            "null, null content",
            ", empty content",
            "\"   \", blank content"
    })
    @SneakyThrows
    void createComment_shouldReturn400_whenContentIsInvalid(String content, String testName) {
        Jwt jwt = AuthFixtures.jwt();
        UserPrincipal userPrincipal = AuthFixtures.userPrincipal();
        Authentication auth = new UserPrincipalAuthenticationToken(userPrincipal, jwt);
        SecurityContextHolder.getContext().setAuthentication(auth);

        Long postId = 1L;

        try {
            mockMvc.perform(post("/posts/{postId}/comment", postId)
                            .contentType("application/json")
                            .content(String.format("""
                                    {
                                        "content": %s
                                    }
                                    """, content)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message").value("Validation Failed"))
                    .andExpect(jsonPath("$.errors[0]").value("Content must not be blank"));

            verify(commentService, never()).createComment(anyLong(), anyLong(), any());
        } finally {
            SecurityContextHolder.clearContext();
        }
    }

    @Test
    @SneakyThrows
    void createComment_shouldReturn500_whenServiceThrowsException() {
        Jwt jwt = AuthFixtures.jwt();
        UserPrincipal userPrincipal = AuthFixtures.userPrincipal();
        Authentication auth = new UserPrincipalAuthenticationToken(userPrincipal, jwt);
        SecurityContextHolder.getContext().setAuthentication(auth);

        Long postId = 1L;
        Long authorId = 1L;

        when(commentService.createComment(anyLong(), anyLong(), any(CommentCreateRequestDTO.class)))
                .thenThrow(new RuntimeException("Database error"));

        try {
            mockMvc.perform(post("/posts/{postId}/comment", postId)
                            .contentType("application/json")
                            .content("""
                                    {
                                        "content": "content"
                                    }
                                    """))
                    .andExpect(status().isInternalServerError())
                    .andExpect(jsonPath("$.message").value("Database error"));

            verify(commentService).createComment(eq(authorId), eq(postId), any(CommentCreateRequestDTO.class));
        } finally {
            SecurityContextHolder.clearContext();
        }
    }

    @Test
    @SneakyThrows
    void deleteComment_shouldDeleteCommentSuccessfully() {
        Jwt jwt = AuthFixtures.jwt();
        UserPrincipal userPrincipal = AuthFixtures.userPrincipal();
        Authentication auth = new UserPrincipalAuthenticationToken(userPrincipal, jwt);
        SecurityContextHolder.getContext().setAuthentication(auth);

        Long commentId = 1L;

        try {
            mockMvc.perform(delete("/comments/{commentId}", commentId))
                    .andExpect(status().isNoContent());

            verify(commentService).deleteCommentById(commentId);
        } finally {
            SecurityContextHolder.clearContext();
        }
    }

    @Test
    @SneakyThrows
    void deleteComment_shouldReturn500_whenServiceThrowsFailedToDeleteCommentException() {
        Jwt jwt = AuthFixtures.jwt();
        UserPrincipal userPrincipal = AuthFixtures.userPrincipal();
        Authentication auth = new UserPrincipalAuthenticationToken(userPrincipal, jwt);
        SecurityContextHolder.getContext().setAuthentication(auth);

        Long commentId = 1L;

        doThrow(new FailedToDeleteCommentException(null))
                .when(commentService).deleteCommentById(commentId);

        try {
            mockMvc.perform(delete("/comments/{commentId}", commentId))
                    .andExpect(status().isInternalServerError())
                    .andExpect(jsonPath("$.message").value("Failed to delete comment"));

            verify(commentService).deleteCommentById(commentId);
        } finally {
            SecurityContextHolder.clearContext();
        }
    }

    @Test
    @SneakyThrows
    void deleteComment_shouldReturn500_whenServiceThrowsUnexpectedException() {
        Jwt jwt = AuthFixtures.jwt();
        UserPrincipal userPrincipal = AuthFixtures.userPrincipal();
        Authentication auth = new UserPrincipalAuthenticationToken(userPrincipal, jwt);
        SecurityContextHolder.getContext().setAuthentication(auth);

        Long commentId = 1L;

        doThrow(new RuntimeException("Unexpected error"))
                .when(commentService).deleteCommentById(commentId);

        try {
            mockMvc.perform(delete("/comments/{commentId}", commentId))
                    .andExpect(status().isInternalServerError())
                    .andExpect(jsonPath("$.message").value("Unexpected error"));

            verify(commentService).deleteCommentById(commentId);
        } finally {
            SecurityContextHolder.clearContext();
        }
    }
}
