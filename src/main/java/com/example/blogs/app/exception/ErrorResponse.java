package com.example.blogs.app.exception;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Standardized error response structure for all API exceptions.
 *
 * @param timestamp Timestamp when the error occurred
 * @param status HTTP status code
 * @param error HTTP status reason phrase
 * @param message Human-readable error message
 * @param path API endpoint path where error occurred
 * @param errors List of validation error messages (empty for non-validation errors)
 */
@Schema(description = "Standard API error response")
public record ErrorResponse(
        @Schema(description = "Timestamp when the error occurred", example = "2024-12-22T02:36:59.123456")
        LocalDateTime timestamp,

        @Schema(description = "HTTP status code", example = "400")
        int status,

        @Schema(description = "HTTP status reason phrase", example = "Bad Request")
        String error,

        @Schema(description = "Human-readable error message", example = "Validation Failed")
        String message,

        @Schema(description = "API endpoint path where error occurred", example = "/auth/register")
        String path,

        @Schema(description = "List of validation error messages (empty for non-validation errors)",
                example = "[\"Username is required\", \"Email must be valid\"]")
        @JsonInclude(JsonInclude.Include.NON_EMPTY)
        List<String> errors
) {
}