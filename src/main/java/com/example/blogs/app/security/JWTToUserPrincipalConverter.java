package com.example.blogs.app.security;

import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

/**
 * Converts JWT tokens to UserPrincipal authentication tokens for Spring Security context.
 */
@Component
public class JWTToUserPrincipalConverter implements Converter<Jwt, AbstractAuthenticationToken> {

    /**
     * Extracts user information from JWT claims and creates an authentication token.
     *
     * @param source the decoded JWT token containing user claims
     * @return authentication token with UserPrincipal and JWT credentials
     */
    @Override
    public AbstractAuthenticationToken convert(Jwt source) {
        UserPrincipal principal = new UserPrincipal(
                Long.parseLong(source.getSubject()),
                source.getClaimAsString("username"),
                source.getClaimAsString("email"),
                source.getClaimAsString("profilePictureUrl")
        );

        return new UserPrincipalAuthenticationToken(principal, source);
    }
}
