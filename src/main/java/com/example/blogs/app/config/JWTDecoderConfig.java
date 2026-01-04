package com.example.blogs.app.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * Provides JWT decoder bean for validating and parsing JWT tokens in Spring Security OAuth2 resource server.
 */
@Configuration
public class JwtDecoderConfig {
    private final SecretKey secretKey;

    /**
     * Constructs JwtDecoderConfig with the configured HMAC SHA256 secret key.
     *
     * @param secret the secret key for JWT verification, loaded from application properties
     */
    public JwtDecoderConfig(@Value("${jwt.secret-key}") String secret) {
        this.secretKey = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
    }

    /**
     * Creates a JWT decoder bean that validates and parses JWT tokens using JJWT library.
     *
     * @return JWT decoder for Spring Security OAuth2 resource server
     */
    @Bean
    public JwtDecoder jwtDecoder() {
        return token -> {
            try {
                Claims claims = Jwts.parser()
                        .verifyWith(secretKey)
                        .build()
                        .parseSignedClaims(token)
                        .getPayload();

                return new Jwt(
                        token,
                        claims.getIssuedAt().toInstant(),
                        claims.getExpiration().toInstant(),
                        Map.of("alg", "HS256", "typ", "JWT"),
                        Map.copyOf(claims)
                );
            } catch (Exception e) {
                throw new JwtException("Invalid JWT token", e);
            }
        };
    }
}
