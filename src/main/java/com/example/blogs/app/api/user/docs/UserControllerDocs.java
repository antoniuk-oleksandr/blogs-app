package com.example.blogs.app.api.user.docs;

import com.example.blogs.app.api.user.dto.UpdateUserResponseDTO;
import com.example.blogs.app.api.user.dto.UserDTO;
import com.example.blogs.app.exception.ErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.http.MediaType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * OpenAPI documentation annotations for user controller endpoints.
 * This class contains meta-annotations that combine multiple Swagger/OpenAPI
 * annotations for cleaner controller code.
 */
public class UserControllerDocs {

    /**
     * Meta-annotation combining all OpenAPI documentation for the get user by username endpoint.
     * <p>
     * Apply this annotation to controller methods to include complete API documentation
     * for retrieving user profiles, including all request/response schemas and examples.
     * </p>
     */
    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @Operation(
            summary = "Get user profile by username",
            description = """
                    Retrieves complete user profile information including bio, profile picture, and all posts.
                                
                    ## Requirements
                    - **Username**: Valid username that exists in the system
                                
                    ## Response
                    Returns user profile data including:
                    - **Username**: The user's unique username
                    - **Bio**: User's biography/description
                    - **Profile Picture URL**: URL to the user's profile picture
                    - **Posts**: List of all posts by the user with summaries
                                
                    ## Use Case
                    Use this endpoint to display a user's profile page with their posts.
                    """,
            tags = {"Users"}
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "User profile retrieved successfully",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = UserDTO.class),
                            examples = @ExampleObject(
                                    name = "User Profile with Posts",
                                    summary = "Complete user profile with post summaries",
                                    description = "Returns user information and their published posts",
                                    value = """
                                            {
                                              "username": "johndoe",
                                              "bio": "Software developer passionate about clean code and best practices.",
                                              "profilePictureUrl": "https://example.com/profiles/johndoe.jpg",
                                              "posts": [
                                                {
                                                  "id": 1,
                                                  "title": "Getting Started with Spring Boot",
                                                  "description": "A comprehensive guide to building REST APIs",
                                                  "createdAt": "2024-12-01T10:30:00",
                                                  "updatedAt": "2024-12-01T10:30:00"
                                                },
                                                {
                                                  "id": 2,
                                                  "title": "Java Best Practices in 2024",
                                                  "description": "Modern Java development techniques",
                                                  "createdAt": "2024-12-15T14:20:00",
                                                  "updatedAt": "2024-12-15T14:20:00"
                                                }
                                              ]
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "User not found - username does not exist",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    name = "User Not Found",
                                    summary = "Requested user does not exist",
                                    value = """
                                            {
                                              "timestamp": "2026-01-16T16:16:26",
                                              "status": 404,
                                              "error": "Not Found",
                                              "message": "User not found with username: johndoe",
                                              "path": "/users/johndoe"
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
                                    summary = "Unexpected error occurred while retrieving user",
                                    value = """
                                            {
                                              "timestamp": "2026-01-16T16:16:26",
                                              "status": 500,
                                              "error": "Internal Server Error",
                                              "message": "An unexpected error occurred while processing your request",
                                              "path": "/users/johndoe"
                                            }
                                            """
                            )
                    )
            )
    })
    public @interface GetUserByUsername {
    }

    /**
     * Meta-annotation combining all OpenAPI documentation for the update user profile endpoint.
     * <p>
     * Apply this annotation to controller methods to include complete API documentation
     * for updating user profiles, including all request/response schemas and examples.
     * </p>
     */
    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @Operation(
            summary = "Update authenticated user's profile",
            description = """
                    Updates the authenticated user's profile information including bio, email, username, password, and profile picture.
                                
                    ## Authentication
                    Requires a valid JWT access token in the Authorization header.
                                
                    ## Supported Updates
                    - **Email**: New email address (must be unique)
                    - **Username**: New username (must be unique)
                    - **Bio**: User biography/description
                    - **Password**: New password (will be hashed before storage)
                    - **Profile Picture**: New profile picture image file (multipart upload)
                                
                    ## Partial Updates
                    All fields are optional. Only provide the fields you want to update.
                    Omitted fields will retain their current values.
                                
                    ## Profile Picture Handling
                    - If a new profile picture is provided, the old one is deleted automatically
                    - Supported formats: JPEG, PNG, GIF
                    - Maximum file size: 5MB
                                
                    ## Security
                    - Password is hashed using BCrypt before storage
                    - Users can only update their own profile
                    - Username and email uniqueness is enforced
                    """,
            tags = {"Users"},
            security = @SecurityRequirement(name = "JWT Bearer Token")
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Profile updated successfully",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = UpdateUserResponseDTO.class),
                            examples = {
                                    @ExampleObject(
                                            name = "Full Profile Update",
                                            summary = "All fields updated including profile picture",
                                            description = "Returns updated profile information",
                                            value = """
                                                    {
                                                      "username": "newusername",
                                                      "bio": "Updated bio: Senior Software Engineer specializing in microservices",
                                                      "profilePictureUrl": "https://example.com/profiles/newusername-abc123.jpg"
                                                    }
                                                    """
                                    ),
                                    @ExampleObject(
                                            name = "Partial Profile Update",
                                            summary = "Only bio updated",
                                            description = "Other fields remain unchanged",
                                            value = """
                                                    {
                                                      "username": "johndoe",
                                                      "bio": "Just updated my bio!",
                                                      "profilePictureUrl": "https://example.com/profiles/johndoe.jpg"
                                                    }
                                                    """
                                    )
                            }
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Bad request - missing required data or invalid format",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = {
                                    @ExampleObject(
                                            name = "Missing Request Body",
                                            summary = "User data part is required but not provided",
                                            value = """
                                                    {
                                                      "timestamp": "2026-02-19T18:30:00",
                                                      "status": 400,
                                                      "error": "Bad Request",
                                                      "message": "Required part 'user' is not present.",
                                                      "path": "/users/me"
                                                    }
                                                    """
                                    ),
                                    @ExampleObject(
                                            name = "Invalid Image Format",
                                            summary = "Profile picture file format not supported",
                                            value = """
                                                    {
                                                      "timestamp": "2026-02-19T18:30:00",
                                                      "status": 400,
                                                      "error": "Bad Request",
                                                      "message": "Invalid file format. Only JPEG, PNG, and GIF are supported.",
                                                      "path": "/users/me"
                                                    }
                                                    """
                                    ),
                                    @ExampleObject(
                                            name = "File Too Large",
                                            summary = "Profile picture exceeds maximum file size",
                                            value = """
                                                    {
                                                      "timestamp": "2026-02-19T18:30:00",
                                                      "status": 400,
                                                      "error": "Bad Request",
                                                      "message": "File size exceeds maximum allowed size of 5MB",
                                                      "path": "/users/me"
                                                    }
                                                    """
                                    )
                            }
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized - missing or invalid authentication token",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = {
                                    @ExampleObject(
                                            name = "Missing Token",
                                            summary = "No JWT token provided in Authorization header",
                                            value = """
                                                    {
                                                      "timestamp": "2026-02-19T18:30:00",
                                                      "status": 401,
                                                      "error": "Unauthorized",
                                                      "message": "Full authentication is required to access this resource",
                                                      "path": "/users/me"
                                                    }
                                                    """
                                    ),
                                    @ExampleObject(
                                            name = "Invalid Token",
                                            summary = "JWT token is malformed or expired",
                                            value = """
                                                    {
                                                      "timestamp": "2026-02-19T18:30:00",
                                                      "status": 401,
                                                      "error": "Unauthorized",
                                                      "message": "Invalid or expired JWT token",
                                                      "path": "/users/me"
                                                    }
                                                    """
                                    )
                            }
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "User not found - authenticated user ID does not exist",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    name = "User Not Found",
                                    summary = "Authenticated user does not exist in database",
                                    value = """
                                            {
                                              "timestamp": "2026-02-19T18:30:00",
                                              "status": 404,
                                              "error": "Not Found",
                                              "message": "User not found",
                                              "path": "/users/me"
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Conflict - username or email already taken by another user",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = {
                                    @ExampleObject(
                                            name = "Username Taken",
                                            summary = "New username is already in use",
                                            value = """
                                                    {
                                                      "timestamp": "2026-02-19T18:30:00",
                                                      "status": 409,
                                                      "error": "Conflict",
                                                      "message": "Username is already taken",
                                                      "path": "/users/me"
                                                    }
                                                    """
                                    ),
                                    @ExampleObject(
                                            name = "Email Taken",
                                            summary = "New email is already in use",
                                            value = """
                                                    {
                                                      "timestamp": "2026-02-19T18:30:00",
                                                      "status": 409,
                                                      "error": "Conflict",
                                                      "message": "Email is already taken",
                                                      "path": "/users/me"
                                                    }
                                                    """
                                    )
                            }
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error - unexpected failure during profile update",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = {
                                    @ExampleObject(
                                            name = "Database Error",
                                            summary = "Failed to update user profile in database",
                                            value = """
                                                    {
                                                      "timestamp": "2026-02-19T18:30:00",
                                                      "status": 500,
                                                      "error": "Internal Server Error",
                                                      "message": "Failed to update user",
                                                      "path": "/users/me"
                                                    }
                                                    """
                                    ),
                                    @ExampleObject(
                                            name = "File Upload Error",
                                            summary = "Failed to upload profile picture to storage",
                                            value = """
                                                    {
                                                      "timestamp": "2026-02-19T18:30:00",
                                                      "status": 500,
                                                      "error": "Internal Server Error",
                                                      "message": "Failed to store file",
                                                      "path": "/users/me"
                                                    }
                                                    """
                                    ),
                                    @ExampleObject(
                                            name = "Unexpected Error",
                                            summary = "General server error",
                                            value = """
                                                    {
                                                      "timestamp": "2026-02-19T18:30:00",
                                                      "status": 500,
                                                      "error": "Internal Server Error",
                                                      "message": "An unexpected error occurred while processing your request",
                                                      "path": "/users/me"
                                                    }
                                                    """
                                    )
                            }
                    )
            )
    })
    public @interface UpdateUserProfile {
    }
}
