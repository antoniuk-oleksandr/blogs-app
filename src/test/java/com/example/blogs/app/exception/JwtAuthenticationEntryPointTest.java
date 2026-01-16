package com.example.blogs.app.exception;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JwtAuthenticationEntryPointTest {

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private FilterChain filterChain;

    @Mock
    private ErrorResponseWriter errorResponseWriter;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    AuthenticationException authException;

    private JwtAuthenticationEntryPoint entryPoint;

    @BeforeEach
    void setUp() {
        entryPoint = new JwtAuthenticationEntryPoint(objectMapper, errorResponseWriter);
    }

    @Test
    @SneakyThrows
    void commence_shouldCallWriteErrorResponse() {
        entryPoint.commence(request, response, authException);

        verify(errorResponseWriter).writeErrorResponse(
                same(response),
                eq(request),
                eq(objectMapper),
                eq(HttpStatus.UNAUTHORIZED),
                eq("Invalid or expired JWT token")
        );
    }

    @Test
    @SneakyThrows
    void commence_shouldHandleJwtException() {
        Throwable cause = new JwtException("Failed", new Exception("Failed"));
        when(authException.getCause()).thenReturn(cause);

        entryPoint.commence(request, response, authException);

        verify(errorResponseWriter).writeErrorResponse(
                same(response),
                eq(request),
                eq(objectMapper),
                eq(HttpStatus.UNAUTHORIZED),
                eq("Invalid or expired JWT token")
        );
    }

    @Test
    @SneakyThrows
    void commence_shouldHandleExpiredJwtException() {
        Throwable cause = new JwtException("Failed", new ExpiredJwtException(null, null, "Expired"));
        when(authException.getCause()).thenReturn(cause);

        entryPoint.commence(request, response, authException);

        verify(errorResponseWriter).writeErrorResponse(
                same(response),
                eq(request),
                eq(objectMapper),
                eq(HttpStatus.UNAUTHORIZED),
                eq("JWT token has expired")
        );
    }
}
