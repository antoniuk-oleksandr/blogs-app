package com.example.blogs.app.api.user.controller;

import com.example.blogs.app.api.user.dto.UserDTO;
import com.example.blogs.app.api.user.dto.UserPostSummaryDto;
import com.example.blogs.app.api.user.exception.FailedToFindUserException;
import com.example.blogs.app.api.user.exception.UserNotFoundException;
import com.example.blogs.app.api.user.service.UserService;
import com.example.blogs.app.exception.ExceptionHttpStatusMapper;
import com.example.blogs.app.exception.GlobalExceptionHandler;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = UserController.class)
@Import({GlobalExceptionHandler.class, ExceptionHttpStatusMapper.class})
@AutoConfigureMockMvc(addFilters = false)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;

    @Test
    @SneakyThrows
    void getUserByUsername_shouldReturn200_whenUserExists() {
        LocalDateTime now = LocalDateTime.now().withNano(0);
        List<UserPostSummaryDto> mockPostSummaries = List.of(
                createMockPostSummary(now),
                createMockPostSummary(now)
        );
        UserDTO userDTO = UserDTO.builder()
                .username("username")
                .bio("bio")
                .profilePictureUrl("profilePictureUrl")
                .posts(mockPostSummaries)
                .build();
        when(userService.getUserByUsername(anyString())).thenReturn(userDTO);

        mockMvc.perform(get("/users/{username}", "username"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("username"))
                .andExpect(jsonPath("$.bio").value("bio"))
                .andExpect(jsonPath("$.profilePictureUrl").value("profilePictureUrl"))
                .andExpect(jsonPath("$.posts").isArray())
                .andExpect(jsonPath("$.posts.length()").value(2))
                .andExpect(jsonPath("$.posts[0].title").value("title"))
                .andExpect(jsonPath("$.posts[0].description").value("description"))
                .andExpect(jsonPath("$.posts[0].slug").value("slug"))
                .andExpect(jsonPath("$.posts[0].previewImageUrl").value("previewImageUrl"))
                .andExpect(jsonPath("$.posts[1].createdAt").value(now.toString()));

        verify(userService).getUserByUsername("username");
    }

    @Test
    @SneakyThrows
    void getUserByUsername_shouldReturn404_whenUserDoesNotExist() {
        when(userService.getUserByUsername(anyString())).thenThrow(new UserNotFoundException());

        mockMvc.perform(get("/users/{username}", "nonexistentuser"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("User not found"));

        verify(userService).getUserByUsername("nonexistentuser");
    }

    @Test
    @SneakyThrows
    void getUserByUsername_shouldReturn500_whenFailedToFindUserExceptionOccurs() {
        when(userService.getUserByUsername(anyString())).thenThrow(new FailedToFindUserException());

        mockMvc.perform(get("/users/{username}", "someuser"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.message").value("Failed to find user"));

        verify(userService).getUserByUsername("someuser");
    }

    private UserPostSummaryDto createMockPostSummary(LocalDateTime time) {
        return UserPostSummaryDto.builder()
                .title("title")
                .description("description")
                .slug("slug")
                .previewImageUrl("previewImageUrl")
                .createdAt(time)
                .build();
    }
}
