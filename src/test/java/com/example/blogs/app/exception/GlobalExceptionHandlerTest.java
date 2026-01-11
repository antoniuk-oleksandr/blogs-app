package com.example.blogs.app.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.FieldError;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.method.annotation.HandlerMethodValidationException;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler exceptionHandler;

    private MethodParameter methodParameter;

    private HttpServletRequest request;

    @BeforeEach
    void setUp() {
        ExceptionHttpStatusMapper mapper = new ExceptionHttpStatusMapper();
        exceptionHandler = new GlobalExceptionHandler(mapper);
        methodParameter = mock(MethodParameter.class);
        request = mock(HttpServletRequest.class);
    }

    @Test
    void handleException_withMethodArgumentNotValidException_shouldReturnValidationErrors() {
        BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(new Object(), "testObject");
        bindingResult.addError(new FieldError("testObject", "username", "Username is required"));
        bindingResult.addError(new FieldError("testObject", "email", "Email is invalid"));

        MethodArgumentNotValidException exception = new MethodArgumentNotValidException(
                methodParameter,
                bindingResult
        );

        ResponseEntity<ErrorResponse> response = exceptionHandler.handleException(exception, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().status()).isEqualTo(400);
        assertThat(response.getBody().message()).isEqualTo("Validation Failed");
        assertThat(response.getBody().errors()).hasSize(2);
        assertThat(response.getBody().errors()).contains("Username is required", "Email is invalid");
    }

    @Test
    void handleException_withHandlerMethodValidationException_shouldReturnValidationErrors() {
        FieldError fieldError1 = new FieldError("object", "field1", "Field 1 error");
        FieldError fieldError2 = new FieldError("object", "field2", "Field 2 error");
        ObjectError objectError = new ObjectError("object", "Object error");

        List<? extends MessageSourceResolvable> allErrors = List.of(fieldError1, fieldError2, objectError);

        HandlerMethodValidationException exception = mock(HandlerMethodValidationException.class);

        doReturn(allErrors).when(exception).getAllErrors();

        ResponseEntity<ErrorResponse> response = exceptionHandler.handleException(exception, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().status()).isEqualTo(400);
        assertThat(response.getBody().message()).isEqualTo("Validation Failed");
        assertThat(response.getBody().errors()).hasSize(3);
        assertThat(response.getBody().errors()).containsExactly(
                "Field 1 error",
                "Field 2 error",
                "Object error"
        );
    }

    @Test
    void handleException_withHttpMessageNotReadableException_shouldReturnRequestBodyRequiredError() {
        HttpMessageNotReadableException exception = mock(HttpMessageNotReadableException.class);

        ResponseEntity<ErrorResponse> response = exceptionHandler.handleException(exception, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().status()).isEqualTo(400);
        assertThat(response.getBody().message()).isEqualTo("Validation Failed");
        assertThat(response.getBody().errors()).hasSize(1);
        assertThat(response.getBody().errors()).contains("Request body is required");
    }

    @Test
    void handleException_withGenericException_shouldReturnInternalServerError() {
        Exception exception = new RuntimeException("Something went wrong");

        ResponseEntity<ErrorResponse> response = exceptionHandler.handleException(exception, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().status()).isEqualTo(500);
        assertThat(response.getBody().message()).isEqualTo("Something went wrong");
        assertThat(response.getBody().errors()).isEmpty();
    }

    @Test
    void handleException_withNullPointerException_shouldReturnInternalServerError() {
        Exception exception = new NullPointerException("Null pointer encountered");

        ResponseEntity<ErrorResponse> response = exceptionHandler.handleException(exception, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().status()).isEqualTo(500);
        assertThat(response.getBody().message()).isEqualTo("Null pointer encountered");
        assertThat(response.getBody().errors()).isEmpty();
    }

    @Test
    void handleException_withIllegalArgumentException_shouldReturnInternalServerError() {
        Exception exception = new IllegalArgumentException("Invalid argument provided");

        ResponseEntity<ErrorResponse> response = exceptionHandler.handleException(exception, request);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().status()).isEqualTo(500);
        assertThat(response.getBody().message()).isEqualTo("Invalid argument provided");
    }

    @Test
    void handleValidationException_withMultipleErrors_shouldReturnAllErrors() {
        BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(new Object(), "user");
        bindingResult.addError(new FieldError("user", "username", "Username is required"));
        bindingResult.addError(new FieldError("user", "password", "Password must be at least 8 characters"));
        bindingResult.addError(new FieldError("user", "email", "Email should be valid"));

        MethodArgumentNotValidException exception = new MethodArgumentNotValidException(
                methodParameter,
                bindingResult
        );

        // When
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleException(exception, request);

        // Then
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().errors()).hasSize(3);
    }

    @Test
    void handleValidationException_withNoErrors_shouldReturnEmptyList() {
        BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(new Object(), "user");

        MethodArgumentNotValidException exception = new MethodArgumentNotValidException(
                methodParameter,
                bindingResult
        );

        ResponseEntity<ErrorResponse> response = exceptionHandler.handleException(exception, request);

        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().errors()).isEmpty();
    }

    @Test
    void handleMethodValidationException_withOnlyObjectErrors_shouldReturnAllErrors() {
        ObjectError error1 = new ObjectError("object", "Error 1");
        ObjectError error2 = new ObjectError("object", "Error 2");

        List<? extends MessageSourceResolvable> allErrors = List.of(error1, error2);

        HandlerMethodValidationException exception = mock(HandlerMethodValidationException.class);

        doReturn(allErrors).when(exception).getAllErrors();

        ResponseEntity<ErrorResponse> response = exceptionHandler.handleException(exception, request);

        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().errors()).containsExactly("Error 1", "Error 2");
    }

    @Test
    void errorResponse_shouldHaveCorrectTimestamp() {
        Exception exception = new RuntimeException("Test");

        ResponseEntity<ErrorResponse> response = exceptionHandler.handleException(exception, request);

        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().timestamp()).isNotNull();
        assertThat(response.getBody().timestamp()).isBeforeOrEqualTo(LocalDateTime.now());
    }
}