package com.example.blogs.app.util;

import org.postgresql.util.PSQLException;
import org.springframework.stereotype.Component;

/**
 * Utility for analyzing PostgreSQL exceptions and extracting constraint violation details.
 */
@Component
public class SqlExceptionUtils {

    /**
     * Checks if an exception chain contains a PostgreSQL unique constraint violation
     * for a specific column.
     *
     * @param t the exception to analyze (including nested causes)
     * @param columnName the column name to check in the constraint violation message
     * @return true if a unique violation on the specified column is found
     */
    public boolean containsUniqueViolation(Throwable t, String columnName) {
        while (t != null) {
            if (t instanceof PSQLException psqlEx
                    && "23505".equals(psqlEx.getSQLState())
                    && psqlEx.getMessage().contains(columnName)) {
                return true;
            }
            t = t.getCause();
        }
        return false;
    }
}
