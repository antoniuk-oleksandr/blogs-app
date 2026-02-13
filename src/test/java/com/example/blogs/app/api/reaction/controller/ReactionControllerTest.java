package com.example.blogs.app.api.reaction.controller;

import com.example.blogs.app.api.auth.fixture.AuthFixtures;
import com.example.blogs.app.api.reaction.dto.ReactionDTO;
import com.example.blogs.app.api.reaction.entity.ReactionType;
import com.example.blogs.app.api.reaction.fixture.ReactionFixtures;
import com.example.blogs.app.api.reaction.service.ReactionService;
import com.example.blogs.app.exception.ErrorResponseWriter;
import com.example.blogs.app.exception.ExceptionHttpStatusMapper;
import com.example.blogs.app.exception.GlobalExceptionHandler;
import com.example.blogs.app.security.UserPrincipal;
import com.example.blogs.app.security.UserPrincipalAuthenticationToken;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc(addFilters = false)
@WebMvcTest(controllers = ReactionController.class)
@Import({
        GlobalExceptionHandler.class,
        ExceptionHttpStatusMapper.class,
        ErrorResponseWriter.class
})
class ReactionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ReactionService reactionService;

    @Test
    @SneakyThrows
    void setReaction_shouldSetReactionSuccessfully() {
        Jwt jwt = AuthFixtures.jwt();
        UserPrincipal userPrincipal = AuthFixtures.userPrincipal();
        Authentication auth = new UserPrincipalAuthenticationToken(userPrincipal, jwt);
        SecurityContextHolder.getContext().setAuthentication(auth);

        Long postId = 1L;
        Long userId = userPrincipal.id();
        ReactionDTO mockDTO = ReactionFixtures.reactionDTO(
                1L, postId, userId, ReactionType.LIKE
        );

        when(reactionService.setReaction(anyLong(), anyLong(), any(ReactionType.class)))
                .thenReturn(mockDTO);

        try {
            mockMvc.perform(post("/posts/{postId}/reactions", postId)
                            .contentType("application/json")
                            .content("""
                                    {
                                        "reactionType": "LIKE"
                                    }
                                    """))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(1L))
                    .andExpect(jsonPath("$.postId").value(postId))
                    .andExpect(jsonPath("$.userId").value(userId))
                    .andExpect(jsonPath("$.reactionType").value("LIKE"));

            verify(reactionService).setReaction(postId, userId, ReactionType.LIKE);
        } finally {
            SecurityContextHolder.clearContext();
        }
    }

    @Test
    @SneakyThrows
    void setReaction_shouldReturn400_whenRequestBodyIsNull() {
        Jwt jwt = AuthFixtures.jwt();
        UserPrincipal userPrincipal = AuthFixtures.userPrincipal();
        Authentication auth = new UserPrincipalAuthenticationToken(userPrincipal, jwt);
        SecurityContextHolder.getContext().setAuthentication(auth);

        Long postId = 1L;

        try {
            mockMvc.perform(post("/posts/{postId}/reactions", postId)
                            .contentType("application/json"))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message").value("Validation Failed"))
                    .andExpect(jsonPath("$.errors[0]").value("Request body is required"));

            verify(reactionService, never()).setReaction(anyLong(), anyLong(), any());
        } finally {
            SecurityContextHolder.clearContext();
        }
    }

    @Test
    @SneakyThrows
    void setReaction_shouldReturn400_whenReactionTypeIsNull() {
        Jwt jwt = AuthFixtures.jwt();
        UserPrincipal userPrincipal = AuthFixtures.userPrincipal();
        Authentication auth = new UserPrincipalAuthenticationToken(userPrincipal, jwt);
        SecurityContextHolder.getContext().setAuthentication(auth);

        Long postId = 1L;

        try {
            mockMvc.perform(post("/posts/{postId}/reactions", postId)
                            .contentType("application/json")
                            .content("""
                                    {
                                        "reactionType": null
                                    }
                                    """))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message").value("Validation Failed"));

            verify(reactionService, never()).setReaction(anyLong(), anyLong(), any());
        } finally {
            SecurityContextHolder.clearContext();
        }
    }

    @Test
    @SneakyThrows
    void setReaction_shouldChangeReactionType() {
        Jwt jwt = AuthFixtures.jwt();
        UserPrincipal userPrincipal = AuthFixtures.userPrincipal();
        Authentication auth = new UserPrincipalAuthenticationToken(userPrincipal, jwt);
        SecurityContextHolder.getContext().setAuthentication(auth);

        Long postId = 1L;
        Long userId = userPrincipal.id();
        ReactionDTO mockDTO = ReactionFixtures.reactionDTO(
                1L, postId, userId, ReactionType.DISLIKE
        );

        when(reactionService.setReaction(anyLong(), anyLong(), any(ReactionType.class)))
                .thenReturn(mockDTO);

        try {
            mockMvc.perform(post("/posts/{postId}/reactions", postId)
                            .contentType("application/json")
                            .content("""
                                    {
                                        "reactionType": "DISLIKE"
                                    }
                                    """))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.reactionType").value("DISLIKE"));

            verify(reactionService).setReaction(postId, userId, ReactionType.DISLIKE);
        } finally {
            SecurityContextHolder.clearContext();
        }
    }

    @Test
    @SneakyThrows
    void setReaction_shouldReturn400_whenReactionTypeIsInvalidEnumValue() {
        Jwt jwt = AuthFixtures.jwt();
        UserPrincipal userPrincipal = AuthFixtures.userPrincipal();
        Authentication auth = new UserPrincipalAuthenticationToken(userPrincipal, jwt);
        SecurityContextHolder.getContext().setAuthentication(auth);

        Long postId = 1L;

        try {
            mockMvc.perform(post("/posts/{postId}/reactions", postId)
                            .contentType("application/json")
                            .content("""
                                    {
                                        "reactionType": "WRONG"
                                    }
                                    """))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message").exists());

            verify(reactionService, never()).setReaction(anyLong(), anyLong(), any());
        } finally {
            SecurityContextHolder.clearContext();
        }
    }

    @Test
    @SneakyThrows
    void setReaction_shouldReturn500_whenServiceThrowsUnexpectedException() {
        Jwt jwt = AuthFixtures.jwt();
        UserPrincipal userPrincipal = AuthFixtures.userPrincipal();
        Authentication auth = new UserPrincipalAuthenticationToken(userPrincipal, jwt);
        SecurityContextHolder.getContext().setAuthentication(auth);

        Long postId = 1L;

        when(reactionService.setReaction(anyLong(), anyLong(), any(ReactionType.class)))
                .thenThrow(new RuntimeException("Database error"));

        try {
            mockMvc.perform(post("/posts/{postId}/reactions", postId)
                            .contentType("application/json")
                            .content("""
                                    {
                                        "reactionType": "LIKE"
                                    }
                                    """))
                    .andExpect(status().isInternalServerError())
                    .andExpect(jsonPath("$.message").value("Database error"));

            verify(reactionService).setReaction(anyLong(), anyLong(), any(ReactionType.class));
        } finally {
            SecurityContextHolder.clearContext();
        }
    }
}
