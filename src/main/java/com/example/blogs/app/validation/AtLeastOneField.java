package com.example.blogs.app.validation;


import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

/**
 * Validates that at least one of the specified fields has a non-blank value.
 */
@Documented
@Constraint(validatedBy = AtLeastOneFieldValidator.class)
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface AtLeastOneField {
    /**
     * The validation error message to display when none of the specified fields have values.
     *
     * @return the error message
     */
    String message() default "At least one field must be provided";

    /**
     * Validation groups to which this constraint belongs.
     *
     * @return the validation groups
     */
    Class<?>[] groups() default {};

    /**
     * Payload for clients to assign custom metadata to this constraint.
     *
     * @return the payload
     */
    Class<? extends Payload>[] payload() default {};

    /**
     * The names of the fields to validate for at least one non-blank value.
     *
     * @return the field names to check
     */
    String[] fields();
}