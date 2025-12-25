package com.example.blogs.app.exception;

import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Centralized exception handler that translates exceptions into standardized error responses.
 * Handles validation errors, domain exceptions, and generic failures.
 */
@AllArgsConstructor
@RestControllerAdvice
public class GlobalExceptionHandler {

    private final ExceptionHttpStatusMapper statusMapper;

    /**
     * Main exception handler that routes different exception types to appropriate handlers.
     *
     * @param exception the exception to handle
     * @param request the HTTP request where the exception occurred
     * @return response entity with structured error information
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(
            Exception exception,
            HttpServletRequest request
    ) {
        return switch (exception) {
            case MethodArgumentNotValidException e -> handleValidationException(e, request);
            case HandlerMethodValidationException e -> handleMethodValidationException(e, request);
            case HttpMessageNotReadableException ignored -> handleMissingRequestBodyException(request);
            default -> handleRegularException(exception, request);
        };
    }

    private ResponseEntity<ErrorResponse> handleValidationException(
            MethodArgumentNotValidException exception,
            HttpServletRequest request
    ) {
        List<String> errors = new ArrayList<>();

        exception.getBindingResult().getAllErrors().forEach(
                error -> errors.add(error.getDefaultMessage())
        );

        ErrorResponse errorResponse = buildValidationErrorResponse(
                errors,
                request.getRequestURI()
        );

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(errorResponse);
    }

    private ResponseEntity<ErrorResponse> handleMethodValidationException(
            HandlerMethodValidationException exception,
            HttpServletRequest request
    ) {
        List<String> errors = new ArrayList<>();
        String path = request.getRequestURI();

        exception.getAllErrors().forEach(error -> {
            if (error instanceof FieldError fieldError) {
                errors.add(fieldError.getDefaultMessage());
            } else {
                errors.add(error.getDefaultMessage());
            }
        });

        ErrorResponse errorResponse = buildValidationErrorResponse(errors, path);

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(errorResponse);
    }

    private ResponseEntity<ErrorResponse> handleMissingRequestBodyException(
            HttpServletRequest request
    ) {
        ErrorResponse errorResponse = buildValidationErrorResponse(
                List.of("Request body is required"),
                request.getRequestURI()
        );

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(errorResponse);
    }

    private ResponseEntity<ErrorResponse> handleRegularException(
            Exception exception,
            HttpServletRequest request
    ) {
        HttpStatus status = this.statusMapper.resolve(exception);

        ErrorResponse errorResponse = new ErrorResponse(
                LocalDateTime.now(),
                status.value(),
                status.getReasonPhrase(),
                exception.getMessage(),
                request.getRequestURI(),
                List.of()
        );

        return ResponseEntity
                .status(status)
                .body(errorResponse);
    }

    private ErrorResponse buildValidationErrorResponse(
            List<String> errors,
            String path
    ) {
        return new ErrorResponse(
                LocalDateTime.now(),
                HttpStatus.BAD_REQUEST.value(),
                HttpStatus.BAD_REQUEST.getReasonPhrase(),
                "Validation Failed",
                path,
                errors
        );
    }
}