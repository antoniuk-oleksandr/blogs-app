package com.example.blogs.app.exception;

import com.example.blogs.app.api.auth.exception.InvalidCredentialsException;
import com.example.blogs.app.api.user.exception.EmailTakenException;
import com.example.blogs.app.api.user.exception.FailedToFindUserException;
import com.example.blogs.app.api.user.exception.UserNotFoundException;
import com.example.blogs.app.api.user.exception.UsernameTakenException;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;

import java.util.Map;

/**
 * Maps domain-specific exceptions to appropriate HTTP status codes.
 * Defaults to 500 INTERNAL_SERVER_ERROR for unmapped exceptions.
 */
@Configuration
public class ExceptionHttpStatusMapper {

    private final Map<Class<? extends Throwable>, HttpStatus> mappings = Map.of(
            UsernameTakenException.class, HttpStatus.CONFLICT,
            EmailTakenException.class, HttpStatus.CONFLICT,
            UserNotFoundException.class, HttpStatus.NOT_FOUND,
            InvalidCredentialsException.class, HttpStatus.UNAUTHORIZED,
            FailedToFindUserException.class, HttpStatus.INTERNAL_SERVER_ERROR
    );

    /**
     * Resolves the HTTP status for a given exception.
     *
     * @param ex the exception to resolve
     * @return corresponding HTTP status or INTERNAL_SERVER_ERROR if not mapped
     */
    public HttpStatus resolve(Throwable ex) {
        return mappings.getOrDefault(
                ex.getClass(),
                HttpStatus.INTERNAL_SERVER_ERROR
        );
    }
}
