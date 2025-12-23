package com.example.blogs.app.api.auth.controller;

import com.example.blogs.app.api.auth.docs.AuthControllerDocs;
import com.example.blogs.app.api.auth.dto.TokenPair;
import com.example.blogs.app.api.auth.dto.RegisterRequest;
import com.example.blogs.app.api.auth.service.AuthService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller for user authentication and registration.
 */
@Tag(name = "Authentication", description = "User authentication and registration endpoints")
@RestController
@RequestMapping("/auth")
public class AuthController {
    private final AuthService authService;

    /**
     * Constructs an AuthController with the required authentication service.
     *
     * @param authService the service handling authentication operations
     */
    public AuthController(AuthService authService) {
        this.authService = authService;
    }

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
}
