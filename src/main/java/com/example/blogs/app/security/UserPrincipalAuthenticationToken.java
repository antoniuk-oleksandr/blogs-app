package com.example.blogs.app.security;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.oauth2.jwt.Jwt;

/**
 * Spring Security authentication token holding UserPrincipal and JWT credentials.
 */
public class UserPrincipalAuthenticationToken extends AbstractAuthenticationToken {

    private final transient UserPrincipal principal;
    private final transient Jwt jwt;

    /**
     * Creates an authenticated token with UserPrincipal and JWT.
     *
     * @param principal the user principal extracted from JWT claims
     * @param jwt       the decoded JWT token
     */
    public UserPrincipalAuthenticationToken(UserPrincipal principal, Jwt jwt) {
        super(null);
        this.principal = principal;
        this.jwt = jwt;
        setAuthenticated(true);
    }

    @Override
    public Object getCredentials() {
        return jwt;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (other == null || getClass() != other.getClass()) {
            return false;
        }
        UserPrincipalAuthenticationToken that = (UserPrincipalAuthenticationToken) other;
        return principal.equals(that.principal) && jwt.equals(that.jwt);
    }

    @Override
    public Object getPrincipal() {
        return principal;
    }


    @Override
    public int hashCode() {
        int result = principal.hashCode();
        result = 31 * result + jwt.hashCode();
        return result;
    }
}