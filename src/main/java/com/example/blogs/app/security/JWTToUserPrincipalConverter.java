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
     * Converts a JWT to a UserPrincipal authentication token.
     * Extracts user information from JWT claims and creates an authenticated token.
     *
     * @param source the JWT to convert
     * @return authenticated token containing UserPrincipal
     * @throws IllegalArgumentException if JWT 'id' claim is missing or invalid
     */
    @Override
    public AbstractAuthenticationToken convert(Jwt source) {
        long id;
        try {
            id = Long.parseLong(source.getClaimAsString("id"));
        } catch (NumberFormatException | NullPointerException e) {
            throw new IllegalArgumentException("JWT 'id' claim is missing or invalid", e);
        }

        UserPrincipal principal = new UserPrincipal(
                id,
                source.getClaimAsString("username"),
                source.getClaimAsString("email"),
                source.getClaimAsString("profilePictureUrl")
        );

        return new UserPrincipalAuthenticationToken(principal, source);
    }
}
