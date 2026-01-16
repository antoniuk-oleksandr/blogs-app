package com.example.blogs.app.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.oauth2.jwt.Jwt;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JWTToUserPrincipalConverterTest {

    @Mock
    private Jwt jwt;

    private JWTToUserPrincipalConverter converter;

    @BeforeEach
    void setUp() {
        converter = new JWTToUserPrincipalConverter();
    }

    @Test
    void convert_shouldReturnUserPrincipalAuthenticationToken() {
        when(jwt.getClaimAsString("id")).thenReturn("1");
        when(jwt.getClaimAsString("username")).thenReturn("username");
        when(jwt.getClaimAsString("email")).thenReturn("email");
        when(jwt.getClaimAsString("profilePictureUrl")).thenReturn("profilePictureUrl");

        AbstractAuthenticationToken result = converter.convert(jwt);

        assertThat(result.getPrincipal())
                .extracting("id", "username", "email", "profilePictureUrl")
                .containsExactly(1L, "username", "email", "profilePictureUrl");

        assertThat(result.getCredentials()).isEqualTo(jwt);
    }


    @Test
    void convert_shouldThrowIllegalArgumentException_whenIdClaimIsMissing() {
        when(jwt.getClaimAsString("id")).thenReturn(null);

        assertThatThrownBy(() -> converter.convert(jwt))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("JWT 'id' claim is missing or invalid");
    }
}
