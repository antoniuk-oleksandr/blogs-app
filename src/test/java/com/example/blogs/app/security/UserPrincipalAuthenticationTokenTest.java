package com.example.blogs.app.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.oauth2.jwt.Jwt;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserPrincipalAuthenticationTokenTest {

    @Mock
    private UserPrincipal principal;

    @Mock
    private Jwt jwt;

    private UserPrincipalAuthenticationToken token;

    @BeforeEach
    void setUp() {
        token = new UserPrincipalAuthenticationToken(principal, jwt);
    }

    @Test
    void getCredentials_shouldReturnJwt() {
        Jwt result = token.getCredentials();

        assertThat(result).isEqualTo(jwt);
    }

    @Test
    void getPrincipal_shouldReturnUserPrincipal() {
        UserPrincipal result = token.getPrincipal();

        assertThat(result).isEqualTo(principal);
    }

    @Test
    void equals_shouldReturnTrueForSameInstance() {
        boolean result = token.equals(token);

        assertThat(result).isTrue();
    }

    @Test
    void equals_shouldReturnFalseForNull() {
        boolean result = token.equals(null);

        assertThat(result).isFalse();
    }

    @Test
    void equals_shouldReturnFalseForDifferentClass() {
        boolean result = token.equals(new Object());

        assertThat(result).isFalse();
    }

    @Test
    void equals_shouldReturnTrueForEqualObjects() {
        UserPrincipalAuthenticationToken otherToken =
                new UserPrincipalAuthenticationToken(principal, jwt);

        boolean result = token.equals(otherToken);

        assertThat(result).isTrue();
    }

    @Test
    void equals_shouldReturnFalseForDifferentPrincipal() {
        UserPrincipal differentPrincipal = mock(UserPrincipal.class);
        UserPrincipalAuthenticationToken otherToken =
                new UserPrincipalAuthenticationToken(differentPrincipal, jwt);

        boolean result = token.equals(otherToken);

        assertThat(result).isFalse();
    }

    @Test
    void equals_shouldReturnFalseForDifferentJwt() {
        Jwt differentJwt = mock(Jwt.class);
        UserPrincipalAuthenticationToken otherToken =
                new UserPrincipalAuthenticationToken(principal, differentJwt);

        boolean result = token.equals(otherToken);

        assertThat(result).isFalse();
    }

    @Test
    void hashCode_shouldReturnCorrectHashCode() {
        int expectedHash = 31 * principal.hashCode() + jwt.hashCode();
        int result = token.hashCode();

        assertThat(result).isEqualTo(expectedHash);
    }
}
