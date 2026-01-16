package com.example.blogs.app.exception;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Writes standardized error responses to HTTP servlet responses.
 */
@Component
@AllArgsConstructor
public class ErrorResponseWriter {

    /**
     * Writes a formatted error response to the HTTP response output stream.
     * Sets appropriate HTTP status code and content type for JSON response.
     *
     * @param response     the HTTP response to write to
     * @param request      the HTTP request that triggered the error
     * @param objectMapper Jackson ObjectMapper for JSON serialization
     * @param status       HTTP status code for the error
     * @param message      error message to include in response
     * @throws IOException if writing to response fails
     */
    public void writeErrorResponse(
            HttpServletResponse response,
            HttpServletRequest request,
            ObjectMapper objectMapper,
            HttpStatus status,
            String message
    ) throws IOException {
        ErrorResponse errorResponse = new ErrorResponse(
                LocalDateTime.now().withNano(0),
                status.value(),
                status.getReasonPhrase(),
                message,
                request.getRequestURI(),
                List.of()
        );

        response.setStatus(status.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
    }
}
