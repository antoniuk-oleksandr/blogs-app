package com.example.blogs.app.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.lang.reflect.Field;
import java.util.Arrays;

/**
 * Validator that checks if at least one specified field contains a non-blank value.
 */
public class AtLeastOneFieldValidator implements ConstraintValidator<AtLeastOneField, Object> {

    private String[] fields;

    @Override
    public void initialize(AtLeastOneField annotation) {
        this.fields = annotation.fields();
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        if (value == null) {
            return false;
        }

        return Arrays.stream(fields)
                .anyMatch(fieldName -> isFieldNotBlank(value, fieldName));
    }

    private boolean isFieldNotBlank(Object object, String fieldName) {
        try {
            Field field = object.getClass().getDeclaredField(fieldName);

            if (!field.canAccess(object) && !field.trySetAccessible()) {
                return false;
            }

            Object fieldValue = field.get(object);

            if (fieldValue == null) {
                return false;
            }

            if (fieldValue instanceof String str) {
                return !str.isBlank();
            }

            return true;
        } catch (NoSuchFieldException | IllegalAccessException e) {
            return false;
        }
    }
}