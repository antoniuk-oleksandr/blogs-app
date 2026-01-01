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
    /**
     * Meta-annotation combining all OpenAPI documentation for the user registration endpoint.
     * <p>
     * Apply this annotation to controller methods to include complete API documentation
     * for user registration, including all request/response schemas and examples.
     * </p>
     */
    public @interface Register {
    }

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
    /**
     * Meta-annotation combining all OpenAPI documentation for the user login endpoint.
     * <p>
     * Apply this annotation to controller methods to include complete API documentation
     * for user login, including all request/response schemas and examples.
     * </p>
     */
    public @interface Login {
    }
}