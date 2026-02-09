package com.example.blogs.app.api.auth.service;

import com.example.blogs.app.api.auth.dto.*;
import com.example.blogs.app.api.auth.entity.RevokedTokenEntity;
import com.example.blogs.app.api.auth.exception.InvalidCredentialsException;
import com.example.blogs.app.api.auth.exception.UnauthorizedException;
import com.example.blogs.app.api.auth.repository.adapter.RevokedTokenRepositoryAdapter;
import com.example.blogs.app.api.user.dto.CreateUserCommand;
import com.example.blogs.app.api.user.entity.UserEntity;
import com.example.blogs.app.api.user.service.UserService;
import com.example.blogs.app.logging.MDCKeys;
import com.example.blogs.app.security.Hasher;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Map;

/**
 * Orchestrates user authentication operations by coordinating password hashing, user validation, and token generation.
 */
@Service
@AllArgsConstructor
public class AuthServiceImpl implements AuthService {

    private static final Logger log = LoggerFactory.getLogger(AuthServiceImpl.class);

    private final UserService userService;

    private final PasswordEncoder passwordEncoder;

    private final TokenPairGenerator tokenPairGenerator;

    private final JWTService jwtService;

    private final RevokedTokenRepositoryAdapter revokedTokenRepositoryAdapter;

    private final Hasher hasher;

    @Override
    public TokenPair register(RegisterRequest registerRequest) {
        String passwordHash = passwordEncoder.encode(registerRequest.password());

        CreateUserCommand command = new CreateUserCommand(
                registerRequest.username(),
                passwordHash,
                registerRequest.email()
        );

        UserEntity user = userService.createUser(command);

        log.info("user_registered userId={} username={} requestId={}",
                user.getId(), user.getUsername(), MDC.get(MDCKeys.REQUEST_ID));

        return tokenPairGenerator.generateTokens(user);
    }

    @Override
    public TokenPair login(LoginRequest loginRequest) {
        UserEntity user;

        try {
            user = userService.getUserByUsernameOrEmail(loginRequest.usernameOrEmail());
        } catch (Exception e) {
            log.warn("authentication_failed reason=user_not_found usernameOrEmail={} requestId={}",
                    loginRequest.usernameOrEmail(), MDC.get(MDCKeys.REQUEST_ID));
            throw new InvalidCredentialsException(e);
        }

        boolean matches = passwordEncoder.matches(loginRequest.password(), user.getPasswordHash());
        if (!matches) {
            log.warn("authentication_failed reason=invalid_password userId={} requestId={}",
                    user.getId(), MDC.get(MDCKeys.REQUEST_ID));
            throw new InvalidCredentialsException(null);
        }

        log.info("user_logged_in userId={} username={} requestId={}",
                user.getId(), user.getUsername(), MDC.get(MDCKeys.REQUEST_ID));

        return tokenPairGenerator.generateTokens(user);
    }

    /**
     * Refreshes an access token by validating the refresh token and generating a new access token.
     * Verifies that the token is valid, not expired, and is of type "refresh".
     *
     * @param tokenRequest request containing the refresh token
     * @return new access token with user claims and updated expiration
     * @throws UnauthorizedException if refresh token is invalid, expired, or not a refresh token type
     */
    @Override
    public AccessTokenResponse refreshAccessToken(RefreshTokenRequest tokenRequest) {
        String tokenHash = hasher.hash(tokenRequest.refreshToken());
        if (revokedTokenRepositoryAdapter.isTokenRevoked(tokenHash)) {
            log.warn("token_refresh_failed reason=token_revoked requestId={}", MDC.get(MDCKeys.REQUEST_ID));
            throw new UnauthorizedException(null);
        }

        Map<String, Object> claims;
        try {
            claims = jwtService.parseClaims(tokenRequest.refreshToken());
        } catch (Exception e) {
            log.warn("token_refresh_failed reason=invalid_token error={} requestId={}",
                    e.getMessage(), MDC.get(MDCKeys.REQUEST_ID));
            throw new UnauthorizedException(e);
        }

        String subject = claims.get("sub").toString();

        if (!"refresh".equals(claims.get("type"))) {
            log.warn("token_refresh_failed reason=wrong_token_type userId={} requestId={}",
                    subject, MDC.get(MDCKeys.REQUEST_ID));
            throw new UnauthorizedException(null);
        }

        Map<String, Object> accessTokenClaims = Map.ofEntries(
                Map.entry("username", claims.get("username")),
                Map.entry("email", claims.get("email")),
                Map.entry("profilePictureUrl", claims.get("profilePictureUrl"))
        );

        String accessToken = jwtService.generateAccessToken(subject, accessTokenClaims);

        log.info("access_token_refreshed userId={} requestId={}",
                claims.get("id"), MDC.get(MDCKeys.REQUEST_ID));

        return new AccessTokenResponse(accessToken);
    }

    @Override
    public RevokedTokenEntity logout(LogoutRequest logoutRequest) {
        Map<String, Object> claims;
        try {
            claims = jwtService.parseClaims(logoutRequest.refreshToken());
        } catch (Exception e) {
            log.warn("logout_failed reason=invalid_token error={} requestId={}",
                    e.getMessage(), MDC.get(MDCKeys.REQUEST_ID));
            throw new UnauthorizedException(e);
        }

        String exp = claims.get("exp").toString();
        LocalDateTime expiresAt = LocalDateTime.ofEpochSecond(
                Long.parseLong(exp), 0, ZoneOffset.UTC
        );

        String tokenHash = hasher.hash(logoutRequest.refreshToken());
        RevokedTokenEntity revokedToken = revokedTokenRepositoryAdapter.saveRevokedToken(tokenHash, expiresAt);

        log.info("user_logged_out userId={} requestId={}",
                claims.get("id"), MDC.get(MDCKeys.REQUEST_ID));

        return revokedToken;
    }
}
