package com.example.blogs.app.api.auth.service;

import com.example.blogs.app.api.auth.exception.FailedToParseClaimsException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
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

    private final JwtParser jwtParser;

    /**
     * Constructs a JWTHelperImpl with the configured secret key.
     *
     * @param secretKey the secret key used for signing tokens, loaded from application properties
     * @param jwtParser the JWT parser for validating and parsing tokens
     */
    public JWTHelperImpl(@Value("${jwt.secret-key}") String secretKey, JwtParser jwtParser) {
        this.signingKey = new SecretKeySpec(
                secretKey.getBytes(StandardCharsets.UTF_8),
                "HmacSHA256"
        );
        this.jwtParser = jwtParser;
    }

    /**
     * Generates a JWT token with no additional claims.
     *
     * @param subject    the token subject (typically user ID)
     * @param expiration time until expiration
     * @return signed JWT token string
     */
    @Override
    public String generateToken(String subject, Duration expiration) {
        return generateToken(subject, new HashMap<>(), expiration);
    }

    /**
     * Generates a JWT token with custom claims and expiration.
     *
     * @param subject    the token subject (typically user ID)
     * @param claims     additional JWT claims to include in payload
     * @param expiration time until expiration
     * @return signed JWT token string
     */
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

    /**
     * Validates a JWT token signature and expiration.
     *
     * @param token the JWT token to validate
     * @return true if token is valid and not expired, false otherwise
     */
    @Override
    public boolean validateToken(String token) {
        try {
            jwtParser.parse(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Parses and extracts claims from a JWT token.
     *
     * @param token the JWT token to parse
     * @return map of claims from the token payload
     * @throws FailedToParseClaimsException if token is malformed or signature is invalid
     */
    @Override
    public Map<String, Object> parseClaims(String token) {
        try {
            Jws<Claims> claimsJws = jwtParser.parseSignedClaims(token);
            return claimsJws.getPayload();
        } catch (Exception e) {
            throw new FailedToParseClaimsException();
        }
    }
}
