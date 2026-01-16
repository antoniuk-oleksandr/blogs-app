package com.example.blogs.app.security;

import com.example.blogs.app.exception.ErrorResponseWriter;
import com.fasterxml.jackson.databind.ObjectMapper;
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

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JwtExceptionFilterTest {

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

    private JwtExceptionFilter filter;

    @BeforeEach
    void setUp() {
        filter = new JwtExceptionFilter(objectMapper, errorResponseWriter);
    }

    @Test
    @SneakyThrows
    void doFilterInternal_shouldCallFilterChain() {
        assertThatCode(() -> filter
                .doFilterInternal(null, null, filterChain)
        ).doesNotThrowAnyException();
        verify(filterChain).doFilter(null, null);
    }

    @Test
    @SneakyThrows
    void doFilterInternal_shouldCallWriteErrorResponseOnException() {
        doThrow(new JwtException("Failed"))
                .when(filterChain)
                .doFilter(request, response);

        filter.doFilterInternal(request, response, filterChain);

        verify(errorResponseWriter).writeErrorResponse(
                same(response),
                eq(request),
                eq(objectMapper),
                eq(HttpStatus.UNAUTHORIZED),
                eq("Invalid or expired JWT token")
        );
    }
}
