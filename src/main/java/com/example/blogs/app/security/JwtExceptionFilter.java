package com.example.blogs.app.security;

import com.example.blogs.app.exception.ErrorResponseWriter;
import com.example.blogs.app.logging.MDCKeys;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Catches JWT exceptions thrown during request processing and writes error responses.
 */
@Component
@AllArgsConstructor
public class JwtExceptionFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(JwtExceptionFilter.class);

    private final ObjectMapper objectMapper;

    private final ErrorResponseWriter errorResponseWriter;

    /**
     * Filters requests and catches JWT exceptions to write standardized error responses.
     * Allows the filter chain to continue for non-JWT exceptions.
     *
     * @param request     the HTTP request
     * @param response    the HTTP response
     * @param filterChain the filter chain to continue
     * @throws ServletException if servlet processing fails
     * @throws IOException      if I/O operation fails
     */
    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        try {
            filterChain.doFilter(request, response);
        } catch (JwtException ex) {
            log.warn("jwt_validation_failed error={} path={} requestId={}",
                    ex.getMessage(), request.getRequestURI(), MDC.get(MDCKeys.REQUEST_ID));
            errorResponseWriter.writeErrorResponse(
                    response,
                    request,
                    objectMapper,
                    HttpStatus.UNAUTHORIZED,
                    "Invalid or expired JWT token"
            );
        }
    }
}