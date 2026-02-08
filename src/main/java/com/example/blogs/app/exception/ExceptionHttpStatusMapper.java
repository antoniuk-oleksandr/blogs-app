package com.example.blogs.app.exception;

import com.example.blogs.app.api.auth.exception.InvalidCredentialsException;
import com.example.blogs.app.api.auth.exception.TokenAlreadyRevokedException;
import com.example.blogs.app.api.auth.exception.UnauthorizedException;
import com.example.blogs.app.api.post.exception.FailedToFindPostBySlugException;
import com.example.blogs.app.api.post.exception.PostNotFoundException;
import com.example.blogs.app.api.user.exception.EmailTakenException;
import com.example.blogs.app.api.user.exception.FailedToFindUserException;
import com.example.blogs.app.api.user.exception.UserNotFoundException;
import com.example.blogs.app.api.user.exception.UsernameTakenException;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;

import java.util.Map;

/**
 * Maps domain-specific exceptions to appropriate HTTP status codes.
 * Defaults to 500 INTERNAL_SERVER_ERROR for unmapped exceptions.
 */
@Configuration
public class ExceptionHttpStatusMapper {

    private final Map<Class<? extends Throwable>, HttpStatus> mappings = Map.ofEntries(
            Map.entry(UsernameTakenException.class, HttpStatus.CONFLICT),
            Map.entry(EmailTakenException.class, HttpStatus.CONFLICT),
            Map.entry(UserNotFoundException.class, HttpStatus.NOT_FOUND),
            Map.entry(InvalidCredentialsException.class, HttpStatus.UNAUTHORIZED),
            Map.entry(FailedToFindUserException.class, HttpStatus.INTERNAL_SERVER_ERROR),
            Map.entry(UnauthorizedException.class, HttpStatus.UNAUTHORIZED),
            Map.entry(TokenAlreadyRevokedException.class, HttpStatus.CONFLICT),
            Map.entry(PostNotFoundException.class, HttpStatus.NOT_FOUND),
            Map.entry(FailedToFindPostBySlugException.class, HttpStatus.INTERNAL_SERVER_ERROR),
            Map.entry(AuthorizationDeniedException.class, HttpStatus.FORBIDDEN),
            Map.entry(MissingServletRequestPartException.class, HttpStatus.BAD_REQUEST)
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
