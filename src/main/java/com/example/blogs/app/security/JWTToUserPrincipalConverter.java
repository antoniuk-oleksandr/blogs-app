package com.example.blogs.app.security;

import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

/**
 * Converts JWT tokens to UserPrincipal authentication tokens for Spring Security context.
 * Extracts user information from custom JWT claims (id, username, email, profilePictureUrl)
 * rather than from the subject claim, which contains a UUID-based JTI.
 */
@Component
public class JWTToUserPrincipalConverter implements Converter<Jwt, AbstractAuthenticationToken> {

    /**
     * Extracts user information from JWT claims and creates an authentication token.
     * Retrieves user ID from the custom "id" claim rather than the subject claim.
     *
     * @param source the decoded JWT token containing user claims
     * @return authentication token with UserPrincipal and JWT credentials
     */
    @Override
    public AbstractAuthenticationToken convert(Jwt source) {
        UserPrincipal principal = new UserPrincipal(
                Long.parseLong(source.getClaimAsString("id")),
                source.getClaimAsString("username"),
                source.getClaimAsString("email"),
                source.getClaimAsString("profilePictureUrl")
        );

        return new UserPrincipalAuthenticationToken(principal, source);
    }
}
