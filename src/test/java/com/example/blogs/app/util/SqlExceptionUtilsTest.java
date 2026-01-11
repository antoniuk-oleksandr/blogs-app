package com.example.blogs.app.util;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.postgresql.util.PSQLException;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SqlExceptionUtilsTest {

    @Mock
    private PSQLException sqlException;

    private SqlExceptionUtils utils;

    @BeforeEach
    void setUp() {
        utils = new SqlExceptionUtils();
    }

    @Test
    void containsUniqueViolation_shouldReturnTrue_whenUniqueViolationOccurs() {
        when(sqlException.getSQLState()).thenReturn("23505");
        when(sqlException.getMessage()).thenReturn("ERROR: duplicate key value violates unique constraint \"users_email_key\"");

        boolean result = utils.containsUniqueViolation(sqlException, "email");
        assertThat(result).isTrue();
        verify(sqlException, times(1)).getSQLState();
        verify(sqlException, times(1)).getMessage();
    }

    @Test
    void containsUniqueViolation_shouldReturnFalse_whenSQLStateDoesNotMatch() {
        when(sqlException.getSQLState()).thenReturn("12345");

        boolean result = utils.containsUniqueViolation(sqlException, "email");
        assertThat(result).isFalse();
        verify(sqlException, times(1)).getSQLState();
        verify(sqlException, times(0)).getMessage();
    }

    @Test
    void containsUniqueViolation_shouldReturnFalse_whenColumnNameDoesNotMatch() {
        when(sqlException.getSQLState()).thenReturn("23505");
        when(sqlException.getMessage()).thenReturn("ERROR: duplicate key value violates unique constraint \"users_username_key\"");

        boolean result = utils.containsUniqueViolation(sqlException, "email");
        assertThat(result).isFalse();
        verify(sqlException, times(1)).getSQLState();
        verify(sqlException, times(1)).getMessage();
    }

    @Test
    void containsUniqueViolation_shouldReturnFalse_whenThrowableIsNotInstanceOfPSQLException() {
        Throwable genericException = new Throwable("Generic exception");

        boolean result = utils.containsUniqueViolation(genericException, "email");
        assertThat(result).isFalse();
        verify(sqlException, times(0)).getSQLState();
        verify(sqlException, times(0)).getMessage();
    }
}
