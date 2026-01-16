package com.example.blogs.app.security;


import com.example.blogs.app.exception.ErrorResponseWriter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import static org.assertj.core.api.Assertions.assertThat;

class ErrorResponseWriterTest {

    private ErrorResponseWriter errorResponseWriter;
    private ObjectMapper objectMapper;
    private MockHttpServletRequest request;
    private MockHttpServletResponse response;

    @BeforeEach
    void setUp() {
        errorResponseWriter = new ErrorResponseWriter();
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();
    }

    @Test
    void writeErrorResponse_shouldWriteCorrectResponse() throws Exception {
        request.setRequestURI("/api/test");
        String message = "Test error message";

        errorResponseWriter.writeErrorResponse(
                response,
                request,
                objectMapper,
                HttpStatus.UNAUTHORIZED,
                message
        );

        assertThat(response.getStatus()).isEqualTo(HttpStatus.UNAUTHORIZED.value());
        assertThat(response.getContentType()).isEqualTo(MediaType.APPLICATION_JSON_VALUE);
        assertThat(response.getContentAsString()).contains("Test error message");
        assertThat(response.getContentAsString()).contains("/api/test");
        assertThat(response.getContentAsString()).contains("401");
        assertThat(response.getContentAsString()).contains("Unauthorized");
    }
}
