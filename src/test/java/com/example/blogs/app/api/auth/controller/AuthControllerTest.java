package com.example.blogs.app.api.auth.controller;

import com.example.blogs.app.api.auth.dto.LoginRequest;
import com.example.blogs.app.api.auth.dto.RegisterRequest;
import com.example.blogs.app.api.auth.dto.TokenPair;
import com.example.blogs.app.api.auth.service.AuthService;
import com.example.blogs.app.exception.ExceptionHttpStatusMapper;
import com.example.blogs.app.exception.GlobalExceptionHandler;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
@Import({GlobalExceptionHandler.class, ExceptionHttpStatusMapper.class})
@AutoConfigureMockMvc(addFilters = false)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AuthService authService;

    @Test
    @SneakyThrows
    void register_shouldReturn201_whenSuccessfulRegistration() {
        when(authService.register(any(RegisterRequest.class))).thenReturn(
                new TokenPair("accessToken", "refreshToken")
        );

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                "username": "test",
                                "password": "test12345",
                                "email": "test@gmail.com"
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.accessToken").exists())
                .andExpect(jsonPath("$.refreshToken").exists());
    }

    @Test
    @SneakyThrows
    void register_shouldReturnRequiredEmail_whenRequestBodyEmailIsNotStated() {
        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "username": "test",
                                  "password": "test12345"
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[0]").value("Email is required"));

    }

    @Test
    @SneakyThrows
    void register_shouldReturnRequiredEmail_whenRequestBodyPasswordIsNotStated() {
        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "username": "test",
                                  "email": "test@gmail.com"
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[*]").value("Password is required"));
    }

    @Test
    @SneakyThrows
    void register_shouldReturnRequiredEmail_whenRequestBodyUsernameIsNotStated() {
        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "password": "test12345",
                                  "email": "test@gmail.com"
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[0]").value("Username is required"));
    }

    @Test
    @SneakyThrows
    void register_shouldReturnRequiredEmail_whenRequestBodyIsNotStated() {
        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[0]").value("Request body is required"));
    }

    @Test
    @SneakyThrows
    void login_shouldReturn200_whenSuccessfulLogin() {
        when(authService.login(any(LoginRequest.class))).thenReturn(
                new TokenPair("accessToken", "refreshToken")
        );

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "usernameOrEmail": "test",
                                    "password": "test12345"
                                }
                                """)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").exists())
                .andExpect(jsonPath("$.refreshToken").exists());
    }

    @Test
    @SneakyThrows
    void login_shouldReturnRequiredEmail_whenRequestBodyIsNotStated() {
        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[0]").value("Request body is required"));
    }

    @Test
    @SneakyThrows
    void login_shouldReturnRequiredEmail_whenRequestBodyPasswordIsNotStated() {
        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "usernameOrEmail": "test"
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[0]").value("Password is required"));
    }

    @Test
    @SneakyThrows
    void login_shouldReturnRequiredEmail_whenRequestBodyUsernameOrEmailIsNotStated() {
        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "password": "test12345"
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[0]").value("Username or email is required"));
    }
}
