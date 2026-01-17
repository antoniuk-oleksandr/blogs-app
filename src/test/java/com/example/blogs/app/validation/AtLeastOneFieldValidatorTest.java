package com.example.blogs.app.validation;

import jakarta.validation.ConstraintValidatorContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AtLeastOneFieldValidatorTest {

    @Mock
    private AtLeastOneField annotation;

    @Mock
    private ConstraintValidatorContext context;

    private AtLeastOneFieldValidator validator;

    @BeforeEach
    void setUp() {
        validator = new AtLeastOneFieldValidator();
        when(annotation.fields()).thenReturn(new String[]{"field1", "field2"});
        validator.initialize(annotation);
    }

    @Test
    void isValid_shouldReturnFalse_whenObjectIsNull() {
        assertThat(validator.isValid(null, context)).isFalse();
    }

    @Test
    void isValid_shouldReturnTrue_whenAtLeastOneFieldHasValue() {
        TestDto dto = new TestDto("value", null);
        assertThat(validator.isValid(dto, context)).isTrue();
    }

    @Test
    void isValid_shouldReturnFalse_whenAllFieldsAreNull() {
        TestDto dto = new TestDto(null, null);
        assertThat(validator.isValid(dto, context)).isFalse();
    }

    @Test
    void isValid_shouldReturnFalse_whenAllFieldsAreBlank() {
        TestDto dto = new TestDto("", "   ");
        assertThat(validator.isValid(dto, context)).isFalse();
    }

    @Test
    void isValid_shouldReturnTrue_whenNonStringFieldIsNotNull() {
        TestDtoWithInt dto = new TestDtoWithInt(null, 42);
        when(annotation.fields()).thenReturn(new String[]{"str", "num"});
        validator.initialize(annotation);

        assertThat(validator.isValid(dto, context)).isTrue();
    }

    @Test
    void isValid_shouldReturnFalse_whenFieldDoesNotExist() {
        TestDto dto = new TestDto("value", null);
        when(annotation.fields()).thenReturn(new String[]{"nonExistentField"});
        validator.initialize(annotation);

        assertThat(validator.isValid(dto, context)).isFalse();
    }

    private record TestDto(String field1, String field2) {
    }

    private record TestDtoWithInt(String str, Integer num) {
    }
}