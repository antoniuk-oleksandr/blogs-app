package com.example.blogs.app.util;

import org.postgresql.util.PSQLException;
import org.springframework.stereotype.Component;

@Component
public class SqlExceptionUtils {

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
