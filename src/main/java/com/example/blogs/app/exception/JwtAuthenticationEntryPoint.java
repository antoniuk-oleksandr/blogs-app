package com.example.blogs.app.exception;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@AllArgsConstructor
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper;

    private final ErrorResponseWriter errorResponseWriter;

    @Override
    public void commence(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException authException
    ) throws IOException {
        String message = "Invalid or expired JWT token";

        Throwable cause = authException.getCause();
        if (cause instanceof JwtException && cause.getCause() instanceof ExpiredJwtException) {
            message = "JWT token has expired";
        }

        errorResponseWriter.writeErrorResponse(
                response,
                request,
                objectMapper,
                HttpStatus.UNAUTHORIZED,
                message
        );
    }
}
