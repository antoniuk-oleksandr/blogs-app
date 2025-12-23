package com.example.blogs.app.api.auth.service;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Generates JWT tokens using JJWT library with HMAC SHA256 signing.
 */
@Getter
@Component
public class JWTHelperImpl implements JWTHelper {
    private final SecretKey signingKey;

    /**
     * Constructs a JWTHelperImpl with the configured secret key.
     *
     * @param secretKey the secret key used for signing tokens, loaded from application properties
     */
    public JWTHelperImpl(@Value("${jwt.secret-key}") String secretKey) {
        signingKey = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
    }

    @Override
    public String generateToken(String subject, Duration expiration) {
        return generateToken(subject, new HashMap<>(), expiration);
    }

    @Override
    public String generateToken(String subject, Map<String, Object> claims, Duration expiration) {
        return Jwts.builder()
                .subject(subject)
                .claims(claims)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expiration.toMillis()))
                .signWith(signingKey)
                .compact();
    }
}
