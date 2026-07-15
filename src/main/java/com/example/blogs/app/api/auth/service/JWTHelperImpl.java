package com.example.blogs.app.api.auth.service;

import com.example.blogs.app.api.auth.exception.FailedToParseClaimsException;
import com.example.blogs.app.logging.MDCKeys;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Generates JWT tokens using JJWT library with HMAC SHA256 signing.
 */
@Getter
@Component
public class JWTHelperImpl implements JWTHelper {

    private static final Logger log = LoggerFactory.getLogger(JWTHelperImpl.class);

    private final SecretKey signingKey;

    private final JwtParser jwtParser;

    private final Clock clock;

    /**
     * Constructs a JWTHelperImpl with the configured secret key.
     *
     * @param secretKey the secret key used for signing tokens, loaded from application properties
     * @param jwtParser the JWT parser for validating and parsing tokens
     * @param clock     the application clock used for token timestamps
     */
    public JWTHelperImpl(@Value("${jwt.secret-key}") String secretKey, JwtParser jwtParser, Clock clock) {
        this.signingKey = new SecretKeySpec(
                secretKey.getBytes(StandardCharsets.UTF_8),
                "HmacSHA256"
        );
        this.jwtParser = jwtParser;
        this.clock = clock;
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
        Instant issuedAt = clock.instant();
        Instant expiresAt = issuedAt.plus(expiration);

        return Jwts.builder()
                .subject(subject)
                .claims(claims)
                .issuedAt(Date.from(issuedAt))
                .expiration(Date.from(expiresAt))
                .signWith(signingKey)
                .compact();
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
            log.warn("jwt_parse_failed error={} requestId={}", e.getMessage(), MDC.get(MDCKeys.REQUEST_ID));
            throw new FailedToParseClaimsException(e);
        }
    }
}
