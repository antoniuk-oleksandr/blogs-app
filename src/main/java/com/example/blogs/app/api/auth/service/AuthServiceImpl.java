package com.example.blogs.app.api.auth.service;

import com.example.blogs.app.api.auth.dto.*;
import com.example.blogs.app.api.auth.exception.InvalidCredentialsException;
import com.example.blogs.app.api.auth.exception.UnauthorizedException;
import com.example.blogs.app.api.user.dto.CreateUserCommand;
import com.example.blogs.app.api.user.entity.UserEntity;
import com.example.blogs.app.api.user.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * Orchestrates user authentication operations by coordinating password hashing, user validation, and token generation.
 */
@Service
@AllArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserService userService;

    private final PasswordEncoder passwordEncoder;

    private final TokenPairGenerator tokenPairGenerator;

    private final JWTService jwtService;

    @Override
    public TokenPair register(RegisterRequest registerRequest) {
        String passwordHash = passwordEncoder.encode(registerRequest.password());

        CreateUserCommand command = new CreateUserCommand(
                registerRequest.username(),
                passwordHash,
                registerRequest.email()
        );

        UserEntity user = userService.createUser(command);

        return tokenPairGenerator.generateTokens(user);
    }

    @Override
    public TokenPair login(LoginRequest loginRequest) {
        UserEntity user;

        try {
            user = userService.findUserByUsernameOrEmail(loginRequest.usernameOrEmail());
        } catch (Exception e) {
            throw new InvalidCredentialsException();
        }

        boolean matches = passwordEncoder.matches(loginRequest.password(), user.getPasswordHash());
        if (!matches) {
            throw new InvalidCredentialsException();
        }

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
        boolean valid = jwtService.validateToken(tokenRequest.refreshToken());
        if (!valid) {
            throw new UnauthorizedException();
        }

        Map<String, Object> claims;
        try {
            claims = jwtService.parseClaims(tokenRequest.refreshToken());
        } catch (Exception e) {
            throw new UnauthorizedException();
        }

        String subject = claims.get("sub").toString();

        if (!"refresh".equals(claims.get("type"))) {
            throw new UnauthorizedException();
        }

        Map<String, Object> accessTokenClaims = Map.ofEntries(
                Map.entry("username", claims.get("username")),
                Map.entry("email", claims.get("email")),
                Map.entry("profilePictureUrl", claims.get("profilePictureUrl"))
        );

        String accessToken = jwtService.generateAccessToken(subject, accessTokenClaims);

        return new AccessTokenResponse(accessToken);
    }
}
