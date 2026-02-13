package com.example.blogs.app.exception;

import com.example.blogs.app.logging.MDCKeys;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
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
import java.util.Arrays;
import java.util.List;

/**
 * Centralized exception handler that translates exceptions into standardized error responses.
 * Handles validation errors, domain exceptions, and generic failures.
 */
@AllArgsConstructor
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    private final ExceptionHttpStatusMapper statusMapper;

    /**
     * Handles validation errors for request body fields annotated with Jakarta validation constraints.
     * Extracts all validation error messages and returns them in a standardized error response.
     *
     * @param exception the validation exception containing binding result errors
     * @param request the HTTP request that triggered the exception
     * @return error response with HTTP 400 status and list of validation errors
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(
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

    /**
     * Handles validation errors for method parameters and path variables.
     * Processes field errors and parameter constraint violations into standardized error messages.
     *
     * @param exception the validation exception containing parameter validation errors
     * @param request the HTTP request that triggered the exception
     * @return error response with HTTP 400 status and list of validation errors
     */
    @ExceptionHandler(HandlerMethodValidationException.class)
    public ResponseEntity<ErrorResponse> handleMethodValidationException(
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

    /**
     * Handles errors when HTTP message body cannot be read or parsed.
     * Processes different scenarios: invalid enum values, unrecognized fields, missing body, or malformed JSON.
     *
     * @param exception the message parsing exception
     * @param request the HTTP request that triggered the exception
     * @return error response with HTTP 400 status and descriptive error message
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleMissingRequestBodyException(
            HttpMessageNotReadableException exception,
            HttpServletRequest request
    ) {
        String errorMessage;

        if (exception.getCause() instanceof InvalidFormatException ife && ife.getTargetType().isEnum()) {
            return handleReactionTypeEnum(ife, request);
        } else if (exception.getMessage() != null && exception.getMessage().contains("Unrecognized field")) {
            errorMessage = "Unrecognized field in request body. Please check field names.";
        } else if (exception.getMessage() != null && exception.getMessage().contains("Required request body is missing")) {
            errorMessage = "Request body is required";
        } else {
            errorMessage = "Malformed JSON request: " + exception.getMostSpecificCause().getMessage();
        }


        ErrorResponse errorResponse = buildValidationErrorResponse(
                List.of(errorMessage),
                request.getRequestURI()
        );

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(errorResponse);
    }

    /**
     * Handles all domain-specific and unhandled exceptions.
     * Maps exceptions to appropriate HTTP status codes using the status mapper.
     * Logs server errors (5xx) with full context for debugging.
     *
     * @param exception the exception to handle
     * @param request the HTTP request that triggered the exception
     * @return error response with appropriate HTTP status and exception details
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleRegularException(
            Exception exception,
            HttpServletRequest request
    ) {
        HttpStatus status = this.statusMapper.resolve(exception);

        if (status.is5xxServerError()) {
            log.error("unhandled_exception exception={} message={} path={} requestId={} userId={}",
                    exception.getClass().getSimpleName(), exception.getMessage(), request.getRequestURI(),
                    MDC.get(MDCKeys.REQUEST_ID), MDC.get(MDCKeys.USER_ID), exception);
        }

        ErrorResponse errorResponse = new ErrorResponse(
                LocalDateTime.now().withNano(0),
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

    /**
     * Handles invalid enum value format errors by providing allowed values.
     * Extracts enum constants and formats a user-friendly error message.
     *
     * @param ife the invalid format exception containing enum type information
     * @param request the HTTP request that triggered the exception
     * @return error response with HTTP 400 status and list of allowed enum values
     */
    private ResponseEntity<ErrorResponse> handleReactionTypeEnum(InvalidFormatException ife, HttpServletRequest request) {
        String allowed = Arrays.toString(ife.getTargetType().getEnumConstants());
        String enumName = ife.getTargetType().getSimpleName();

        return ResponseEntity.badRequest().body(
                buildValidationErrorResponse(
                        List.of("Invalid value for field '" + enumName + "'. Allowed values: " + allowed),
                        request.getRequestURI()
                )
        );
    }

    /**
     * Builds a standardized error response for validation failures.
     *
     * @param errors list of validation error messages
     * @param path the request URI path where the error occurred
     * @return error response with HTTP 400 status, timestamp, and error details
     */
    private ErrorResponse buildValidationErrorResponse(
            List<String> errors,
            String path
    ) {
        return new ErrorResponse(
                LocalDateTime.now().withNano(0),
                HttpStatus.BAD_REQUEST.value(),
                HttpStatus.BAD_REQUEST.getReasonPhrase(),
                "Validation Failed",
                path,
                errors
        );
    }
}