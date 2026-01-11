package com.example.blogs.app.api.auth.docs;

import com.example.blogs.app.api.auth.dto.TokenPair;
import com.example.blogs.app.exception.ErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.MediaType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * OpenAPI documentation annotations for authentication controller endpoints.
 * This class contains meta-annotations that combine multiple Swagger/OpenAPI
 * annotations for cleaner controller code.
 */
public class AuthControllerDocs {

    /**
     * Meta-annotation combining all OpenAPI documentation for the user registration endpoint.
     * <p>
     * Apply this annotation to controller methods to include complete API documentation
     * for user registration, including all request/response schemas and examples.
     * </p>
     */
    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @Operation(
            summary = "Register a new user",
            description = """
                    Creates a new user account and returns JWT authentication tokens.
                                
                    ## Requirements
                    - **Username**: 3-16 characters, must be unique
                    - **Email**: Valid email format, must be unique
                    - **Password**: Minimum 8 characters
                                
                    ## Response
                    Returns a token pair upon successful registration:
                    - **Access Token**: Short-lived token for API authentication (15 min)
                    - **Refresh Token**: Long-lived token for obtaining new access tokens (30 days)
                                
                    ## Security
                    - Password is hashed using BCrypt before storage
                    - Tokens are signed using HS256 algorithm
                    - No sensitive data is included in JWT payload
                    """,
            tags = {"Authentication"}
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "User successfully registered and authenticated",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = TokenPair.class),
                            examples = @ExampleObject(
                                    name = "Successful Registration",
                                    summary = "New user account created",
                                    description = "Returns JWT tokens for immediate authentication",
                                    value = """
                                            {
                                              "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJqb2huZG9lIiwiaWF0IjoxNzAzMjU2MDAwLCJleHAiOjE3MDMyNTY5MDB9.signature",
                                              "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJqb2huZG9lIiwiaWF0IjoxNzAzMjU2MDAwLCJleHAiOjE3MDM4NjA4MDB9.signature"
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Validation failed - invalid or missing request data",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = {
                                    @ExampleObject(
                                            name = "Missing Required Fields",
                                            summary = "One or more required fields are missing",
                                            value = """
                                                    {
                                                      "timestamp": "2024-12-22T02:36:59.123456",
                                                      "status": 400,
                                                      "error": "Bad Request",
                                                      "message": "Validation Failed",
                                                      "path": "/auth/register",
                                                      "errors": [
                                                        "Username is required",
                                                        "Email is required",
                                                        "Password is required"
                                                      ]
                                                    }
                                                    """
                                    ),
                                    @ExampleObject(
                                            name = "Invalid Email Format",
                                            summary = "Email address format is invalid",
                                            value = """
                                                    {
                                                      "timestamp": "2024-12-22T02:36:59.123456",
                                                      "status": 400,
                                                      "error": "Bad Request",
                                                      "message": "Validation Failed",
                                                      "path": "/auth/register",
                                                      "errors": [
                                                        "Email must be valid"
                                                      ]
                                                    }
                                                    """
                                    ),
                                    @ExampleObject(
                                            name = "Password Too Short",
                                            summary = "Password does not meet minimum length requirement",
                                            value = """
                                                    {
                                                      "timestamp": "2024-12-22T02:36:59.123456",
                                                      "status": 400,
                                                      "error": "Bad Request",
                                                      "message": "Validation Failed",
                                                      "path": "/auth/register",
                                                      "errors": [
                                                        "Password must be at least 8 characters"
                                                      ]
                                                    }
                                                    """
                                    ),
                                    @ExampleObject(
                                            name = "Username Invalid Length",
                                            summary = "Username does not meet length requirements",
                                            value = """
                                                    {
                                                      "timestamp": "2024-12-22T02:36:59.123456",
                                                      "status": 400,
                                                      "error": "Bad Request",
                                                      "message": "Validation Failed",
                                                      "path": "/auth/register",
                                                      "errors": [
                                                        "Username must be between 3 and 16 characters"
                                                      ]
                                                    }
                                                    """
                                    ),
                                    @ExampleObject(
                                            name = "Multiple Validation Errors",
                                            summary = "Multiple fields failed validation",
                                            value = """
                                                    {
                                                      "timestamp": "2024-12-22T02:36:59.123456",
                                                      "status": 400,
                                                      "error": "Bad Request",
                                                      "message": "Validation Failed",
                                                      "path": "/auth/register",
                                                      "errors": [
                                                        "Username must be between 3 and 16 characters",
                                                        "Email must be valid",
                                                        "Password must be at least 8 characters"
                                                      ]
                                                    }
                                                    """
                                    ),
                                    @ExampleObject(
                                            name = "Missing Request Body",
                                            summary = "Request body is required but not provided",
                                            value = """
                                                    {
                                                      "timestamp": "2024-12-22T02:36:59.123456",
                                                      "status": 400,
                                                      "error": "Bad Request",
                                                      "message": "Validation Failed",
                                                      "path": "/auth/register",
                                                      "errors": [
                                                        "Request body is required"
                                                      ]
                                                    }
                                                    """
                                    )
                            }
                    )
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Conflict - username or email already exists",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = {
                                    @ExampleObject(
                                            name = "Duplicate Username",
                                            summary = "Username is already taken",
                                            value = """
                                                    {
                                                      "timestamp": "2024-12-22T02:36:59.123456",
                                                      "status": 409,
                                                      "error": "Conflict",
                                                      "message": "Username already exists",
                                                      "path": "/auth/register"
                                                    }
                                                    """
                                    ),
                                    @ExampleObject(
                                            name = "Duplicate Email",
                                            summary = "Email is already taken",
                                            value = """
                                                    {
                                                      "timestamp": "2024-12-22T02:36:59.123456",
                                                      "status": 409,
                                                      "error": "Conflict",
                                                      "message": "Email already exists",
                                                      "path": "/auth/register"
                                                    }
                                                    """
                                    )
                            }
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error - unexpected failure",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    name = "Server Error",
                                    summary = "Unexpected error occurred during registration",
                                    value = """
                                            {
                                              "timestamp": "2024-12-22T02:36:59.123456",
                                              "status": 500,
                                              "error": "Internal Server Error",
                                              "message": "An unexpected error occurred while processing your request",
                                              "path": "/auth/register"
                                            }
                                            """
                            )
                    )
            )
    })
    public @interface Register {
    }

    /**
     * Meta-annotation combining all OpenAPI documentation for the user login endpoint.
     * <p>
     * Apply this annotation to controller methods to include complete API documentation
     * for user login, including all request/response schemas and examples.
     * </p>
     */
    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @Operation(
            summary = "Login user",
            description = """
                    Authenticates an existing user and returns JWT authentication tokens.
                                
                    ## Requirements
                    - **Username or Email**: Valid username or email address
                    - **Password**: User's password
                                
                    ## Response
                    Returns a token pair upon successful authentication:
                    - **Access Token**: Short-lived token for API authentication (15 min)
                    - **Refresh Token**: Long-lived token for obtaining new access tokens (30 days)
                                
                    ## Security
                    - Password is verified using BCrypt
                    - Tokens are signed using HS256 algorithm
                    - No sensitive data is included in JWT payload
                    """,
            tags = {"Authentication"}
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "User successfully authenticated",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = TokenPair.class),
                            examples = @ExampleObject(
                                    name = "Successful Login",
                                    summary = "User authenticated successfully",
                                    description = "Returns JWT tokens for authentication",
                                    value = """
                                            {
                                              "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJqb2huZG9lIiwiaWF0IjoxNzAzMjU2MDAwLCJleHAiOjE3MDMyNTY5MDB9.signature",
                                              "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJqb2huZG9lIiwiaWF0IjoxNzAzMjU2MDAwLCJleHAiOjE3MDM4NjA4MDB9.signature"
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Validation failed - invalid or missing request data",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = {
                                    @ExampleObject(
                                            name = "Missing Required Fields",
                                            summary = "One or more required fields are missing",
                                            value = """
                                                    {
                                                      "timestamp": "2024-12-22T02:36:59.123456",
                                                      "status": 400,
                                                      "error": "Bad Request",
                                                      "message": "Validation Failed",
                                                      "path": "/auth/login",
                                                      "errors": [
                                                        "Username or email is required",
                                                        "Password is required"
                                                      ]
                                                    }
                                                    """
                                    ),
                                    @ExampleObject(
                                            name = "Missing Request Body",
                                            summary = "Request body is required but not provided",
                                            value = """
                                                    {
                                                      "timestamp": "2024-12-22T02:36:59.123456",
                                                      "status": 400,
                                                      "error": "Bad Request",
                                                      "message": "Validation Failed",
                                                      "path": "/auth/login",
                                                      "errors": [
                                                        "Request body is required"
                                                      ]
                                                    }
                                                    """
                                    )
                            }
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized - invalid credentials",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    name = "Invalid Credentials",
                                    summary = "Username/email or password is incorrect",
                                    value = """
                                            {
                                              "timestamp": "2024-12-22T02:36:59.123456",
                                              "status": 401,
                                              "error": "Unauthorized",
                                              "message": "Invalid credentials",
                                              "path": "/auth/login"
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error - unexpected failure",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    name = "Server Error",
                                    summary = "Unexpected error occurred during login",
                                    value = """
                                            {
                                              "timestamp": "2024-12-22T02:36:59.123456",
                                              "status": 500,
                                              "error": "Internal Server Error",
                                              "message": "An unexpected error occurred while processing your request",
                                              "path": "/auth/login"
                                            }
                                            """
                            )
                    )
            )
    })
    public @interface Login {
    }

    /**
     * Meta-annotation combining all OpenAPI documentation for the get current user endpoint.
     * <p>
     * Apply this annotation to controller methods to include complete API documentation
     * for retrieving authenticated user information.
     * </p>
     */
    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @Operation(
            summary = "Get authenticated user information",
            description = """
                    Returns the currently authenticated user's principal information extracted from the JWT token.
                                
                    ## Requirements
                    - Valid JWT access token must be provided in Authorization header
                    - Token must not be expired
                                
                    ## Response
                    Returns user principal containing:
                    - **User ID**: Unique user identifier
                    - **Username**: User's username
                    - **Email**: User's email address
                    - **Profile Picture URL**: URL to user's profile picture (if set)
                                
                    ## Security
                    - Requires authentication via Bearer token
                    - Only returns information for the authenticated user
                    """,
            tags = {"Authentication"}
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Successfully retrieved authenticated user information",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = com.example.blogs.app.security.UserPrincipal.class),
                            examples = @ExampleObject(
                                    name = "User Principal",
                                    summary = "Authenticated user information",
                                    description = "User details extracted from JWT token",
                                    value = """
                                            {
                                              "id": 123,
                                              "username": "johndoe",
                                              "email": "johndoe@example.com",
                                              "profilePictureUrl": "https://example.com/profile.jpg"
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized - missing, invalid, or expired token",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = {
                                    @ExampleObject(
                                            name = "Missing Authorization Header",
                                            summary = "No authentication token provided",
                                            value = """
                                                    {
                                                      "timestamp": "2024-12-22T02:36:59.123456",
                                                      "status": 401,
                                                      "error": "Unauthorized",
                                                      "message": "Full authentication is required to access this resource",
                                                      "path": "/auth/me"
                                                    }
                                                    """
                                    ),
                                    @ExampleObject(
                                            name = "Invalid Token",
                                            summary = "Token is malformed or signature is invalid",
                                            value = """
                                                    {
                                                      "timestamp": "2024-12-22T02:36:59.123456",
                                                      "status": 401,
                                                      "error": "Unauthorized",
                                                      "message": "Invalid JWT token",
                                                      "path": "/auth/me"
                                                    }
                                                    """
                                    ),
                                    @ExampleObject(
                                            name = "Expired Token",
                                            summary = "Token has expired",
                                            value = """
                                                    {
                                                      "timestamp": "2024-12-22T02:36:59.123456",
                                                      "status": 401,
                                                      "error": "Unauthorized",
                                                      "message": "Token has expired",
                                                      "path": "/auth/me"
                                                    }
                                                    """
                                    )
                            }
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error - unexpected failure",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    name = "Server Error",
                                    summary = "Unexpected error occurred",
                                    value = """
                                            {
                                              "timestamp": "2024-12-22T02:36:59.123456",
                                              "status": 500,
                                              "error": "Internal Server Error",
                                              "message": "An unexpected error occurred while processing your request",
                                              "path": "/auth/me"
                                            }
                                            """
                            )
                    )
            )
    })
    public @interface Me {
    }

    /**
     * Meta-annotation combining all OpenAPI documentation for the refresh token endpoint.
     * <p>
     * Apply this annotation to controller methods to include complete API documentation
     * for refreshing access tokens, including all request/response schemas and examples.
     * </p>
     */
    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @Operation(
            summary = "Refresh access token",
            description = """
                    Generates a new access token using a valid refresh token.
                                
                    ## Requirements
                    - Valid refresh token obtained from login or registration
                    - Token must not be expired
                    - Token type must be "refresh"
                                
                    ## Response
                    Returns a new access token with updated expiration:
                    - **Access Token**: Short-lived token for API authentication (15 min)
                                
                    ## Use Case
                    Use this endpoint when your access token has expired but your refresh token is still valid.
                    This allows maintaining user session without requiring re-authentication.
                                
                    ## Security
                    - Refresh token is validated and verified
                    - User claims are preserved from the original refresh token
                    - Only refresh token type is accepted (access tokens will be rejected)
                    """,
            tags = {"Authentication"}
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Successfully refreshed access token",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = com.example.blogs.app.api.auth.dto.AccessTokenResponse.class),
                            examples = @ExampleObject(
                                    name = "New Access Token",
                                    summary = "New access token generated",
                                    description = "Fresh access token with updated expiration",
                                    value = """
                                            {
                                              "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjMiLCJ1c2VybmFtZSI6ImpvaG5kb2UiLCJpYXQiOjE3MDMyNTYwMDAsImV4cCI6MTcwMzI1NjkwMH0.signature"
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Validation failed - invalid or missing request data",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = {
                                    @ExampleObject(
                                            name = "Missing Refresh Token",
                                            summary = "Refresh token is required but not provided",
                                            value = """
                                                    {
                                                      "timestamp": "2024-12-22T02:36:59.123456",
                                                      "status": 400,
                                                      "error": "Bad Request",
                                                      "message": "Validation Failed",
                                                      "path": "/auth/refresh",
                                                      "errors": [
                                                        "Refresh token is required"
                                                      ]
                                                    }
                                                    """
                                    ),
                                    @ExampleObject(
                                            name = "Missing Request Body",
                                            summary = "Request body is required but not provided",
                                            value = """
                                                    {
                                                      "timestamp": "2024-12-22T02:36:59.123456",
                                                      "status": 400,
                                                      "error": "Bad Request",
                                                      "message": "Validation Failed",
                                                      "path": "/auth/refresh",
                                                      "errors": [
                                                        "Request body is required"
                                                      ]
                                                    }
                                                    """
                                    )
                            }
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized - invalid, expired, or wrong token type",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = {
                                    @ExampleObject(
                                            name = "Invalid Refresh Token",
                                            summary = "Refresh token is invalid or malformed",
                                            value = """
                                                    {
                                                      "timestamp": "2024-12-22T02:36:59.123456",
                                                      "status": 401,
                                                      "error": "Unauthorized",
                                                      "message": "Unauthorized access",
                                                      "path": "/auth/refresh"
                                                    }
                                                    """
                                    ),
                                    @ExampleObject(
                                            name = "Expired Refresh Token",
                                            summary = "Refresh token has expired",
                                            value = """
                                                    {
                                                      "timestamp": "2024-12-22T02:36:59.123456",
                                                      "status": 401,
                                                      "error": "Unauthorized",
                                                      "message": "Unauthorized access",
                                                      "path": "/auth/refresh"
                                                    }
                                                    """
                                    ),
                                    @ExampleObject(
                                            name = "Wrong Token Type",
                                            summary = "Access token provided instead of refresh token",
                                            value = """
                                                    {
                                                      "timestamp": "2024-12-22T02:36:59.123456",
                                                      "status": 401,
                                                      "error": "Unauthorized",
                                                      "message": "Unauthorized access",
                                                      "path": "/auth/refresh"
                                                    }
                                                    """
                                    ),
                                    @ExampleObject(
                                            name = "Failed to Parse Claims",
                                            summary = "Token claims cannot be parsed",
                                            value = """
                                                    {
                                                      "timestamp": "2024-12-22T02:36:59.123456",
                                                      "status": 401,
                                                      "error": "Unauthorized",
                                                      "message": "Unauthorized access",
                                                      "path": "/auth/refresh"
                                                    }
                                                    """
                                    )
                            }
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error - unexpected failure",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    name = "Server Error",
                                    summary = "Unexpected error occurred during token refresh",
                                    value = """
                                            {
                                              "timestamp": "2024-12-22T02:36:59.123456",
                                              "status": 500,
                                              "error": "Internal Server Error",
                                              "message": "An unexpected error occurred while processing your request",
                                              "path": "/auth/refresh"
                                            }
                                            """
                            )
                    )
            )
    })
    public @interface Refresh {
    }

    /**
     * Meta-annotation combining all OpenAPI documentation for the logout endpoint.
     * <p>
     * Apply this annotation to controller methods to include complete API documentation
     * for user logout and token revocation, including all request/response schemas and examples.
     * </p>
     */
    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @Operation(
            summary = "Logout user and revoke refresh token",
            description = """
                    Revokes a refresh token to invalidate all active sessions using that token.
                                
                    ## Requirements
                    - Valid refresh token obtained from login or registration
                    - Token must not have been previously revoked
                                
                    ## Response
                    Returns HTTP 204 No Content on successful revocation
                                
                    ## Use Case
                    Use this endpoint when a user explicitly logs out or when you need to invalidate
                    a specific refresh token for security purposes (e.g., device logout).
                                
                    ## Security
                    - Token is hashed before storage to prevent token exposure
                    - Expired tokens are automatically cleaned up by scheduled task
                    - Once revoked, the token cannot be used to refresh access tokens
                    - Duplicate revocation attempts return 409 Conflict
                    """,
            tags = {"Authentication"}
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "Token successfully revoked - logout successful",
                    content = @Content()
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Validation failed - invalid or missing request data",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = {
                                    @ExampleObject(
                                            name = "Missing Refresh Token",
                                            summary = "Refresh token is required but not provided",
                                            value = """
                                                    {
                                                      "timestamp": "2024-12-22T02:36:59.123456",
                                                      "status": 400,
                                                      "error": "Bad Request",
                                                      "message": "Validation Failed",
                                                      "path": "/auth/logout",
                                                      "errors": [
                                                        "Refresh token is required"
                                                      ]
                                                    }
                                                    """
                                    ),
                                    @ExampleObject(
                                            name = "Missing Request Body",
                                            summary = "Request body is required but not provided",
                                            value = """
                                                    {
                                                      "timestamp": "2024-12-22T02:36:59.123456",
                                                      "status": 400,
                                                      "error": "Bad Request",
                                                      "message": "Validation Failed",
                                                      "path": "/auth/logout",
                                                      "errors": [
                                                        "Request body is required"
                                                      ]
                                                    }
                                                    """
                                    )
                            }
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized - invalid or expired token",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = {
                                    @ExampleObject(
                                            name = "Invalid Refresh Token",
                                            summary = "Refresh token is invalid or malformed",
                                            value = """
                                                    {
                                                      "timestamp": "2024-12-22T02:36:59.123456",
                                                      "status": 401,
                                                      "error": "Unauthorized",
                                                      "message": "Unauthorized access",
                                                      "path": "/auth/logout"
                                                    }
                                                    """
                                    ),
                                    @ExampleObject(
                                            name = "Expired Refresh Token",
                                            summary = "Refresh token has expired",
                                            value = """
                                                    {
                                                      "timestamp": "2024-12-22T02:36:59.123456",
                                                      "status": 401,
                                                      "error": "Unauthorized",
                                                      "message": "Unauthorized access",
                                                      "path": "/auth/logout"
                                                    }
                                                    """
                                    )
                            }
                    )
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Conflict - token has already been revoked",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    name = "Token Already Revoked",
                                    summary = "This token was already revoked in a previous logout",
                                    value = """
                                            {
                                              "timestamp": "2024-12-22T02:36:59.123456",
                                              "status": 409,
                                              "error": "Conflict",
                                              "message": "Token is already revoked.",
                                              "path": "/auth/logout"
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error - unexpected failure",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    name = "Server Error",
                                    summary = "Unexpected error occurred during logout",
                                    value = """
                                            {
                                              "timestamp": "2024-12-22T02:36:59.123456",
                                              "status": 500,
                                              "error": "Internal Server Error",
                                              "message": "An unexpected error occurred while processing your request",
                                              "path": "/auth/logout"
                                            }
                                            """
                            )
                    )
            )
    })
    public @interface Logout {
    }
}