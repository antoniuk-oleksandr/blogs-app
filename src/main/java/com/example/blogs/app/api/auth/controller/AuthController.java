package com.example.blogs.app.api.auth.controller;

import com.example.blogs.app.api.auth.docs.AuthControllerDocs;
import com.example.blogs.app.api.auth.dto.*;
import com.example.blogs.app.security.UserPrincipal;
import com.example.blogs.app.api.auth.service.AuthService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

/**
 * Controller for user authentication and registration.
 */
@Tag(name = "Authentication", description = "User authentication and registration endpoints")
@RestController
@RequestMapping("/auth")
@AllArgsConstructor
public class AuthController {

    private final AuthService authService;

    /**
     * Registers a new user and returns authentication tokens.
     *
     * @param registerRequest the registration details including username, email, and password
     * @return HTTP 201 with access and refresh tokens
     */
    @AuthControllerDocs.Register
    @PostMapping("/register")
    public ResponseEntity<TokenPair> register(@NotNull @Valid @RequestBody RegisterRequest registerRequest) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(authService.register(registerRequest));
    }

    /**
     * Authenticates a user and returns authentication tokens.
     *
     * @param loginRequest the login credentials including username/email and password
     * @return HTTP 200 with access and refresh tokens
     */
    @PostMapping("/login")
    @AuthControllerDocs.Login
    public ResponseEntity<TokenPair> login(@NotNull @Valid @RequestBody LoginRequest loginRequest) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(authService.login(loginRequest));
    }

    /**
     * Returns the authenticated user's principal information.
     *
     * @param user the authenticated user principal from the JWT token
     * @return HTTP 200 with user principal details
     */
    @AuthControllerDocs.Me
    @GetMapping("/me")
    public ResponseEntity<UserPrincipal> me(@AuthenticationPrincipal UserPrincipal user) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(user);
    }

    /**
     * Refreshes the access token using a valid refresh token.
     *
     * @param tokenRequest the refresh token request containing the refresh token
     * @return HTTP 200 with a new access token
     */
    @AuthControllerDocs.Refresh
    @PostMapping("/refresh")
    public ResponseEntity<AccessTokenResponse> refreshToken(@NotNull @Valid @RequestBody RefreshTokenRequest tokenRequest) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(authService.refreshAccessToken(tokenRequest));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@NotNull @Valid @RequestBody LogoutRequest logoutRequest) {
        authService.logout(logoutRequest);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
