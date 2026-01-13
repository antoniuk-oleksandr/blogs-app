package com.example.blogs.app.api.user.docs;

import com.example.blogs.app.api.user.dto.UserDTO;
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
                                              "timestamp": "2024-12-22T02:36:59.123456",
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
                                              "timestamp": "2024-12-22T02:36:59.123456",
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
}
