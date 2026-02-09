package com.example.blogs.app.exception;

import com.example.blogs.app.logging.MDCKeys;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Handles JWT authentication failures by writing appropriate error responses.
 */
@Component
@AllArgsConstructor
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private static final Logger log = LoggerFactory.getLogger(JwtAuthenticationEntryPoint.class);

    private final ObjectMapper objectMapper;

    private final ErrorResponseWriter errorResponseWriter;

    /**
     * Handles authentication exceptions by determining the error type and writing an appropriate response.
     * Distinguishes between expired JWT tokens and other JWT validation failures.
     *
     * @param request       the HTTP request that triggered authentication
     * @param response      the HTTP response to write to
     * @param authException the authentication exception that occurred
     * @throws IOException if writing to response fails
     */
    @Override
    public void commence(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException authException
    ) throws IOException {
        String message = "Invalid or expired JWT token";
        String reason = "invalid_token";

        Throwable cause = authException.getCause();
        if (cause instanceof JwtException && cause.getCause() instanceof ExpiredJwtException) {
            message = "JWT token has expired";
            reason = "token_expired";
        }

        log.warn("authentication_failed reason={} path={} requestId={}",
                reason, request.getRequestURI(), MDC.get(MDCKeys.REQUEST_ID));

        errorResponseWriter.writeErrorResponse(
                response,
                request,
                objectMapper,
                HttpStatus.UNAUTHORIZED,
                message
        );
    }
}
