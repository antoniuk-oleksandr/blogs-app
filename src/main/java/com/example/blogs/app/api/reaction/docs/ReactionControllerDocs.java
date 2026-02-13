package com.example.blogs.app.api.reaction.docs;

import com.example.blogs.app.api.reaction.dto.ReactionDTO;
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
 * OpenAPI documentation annotations for reaction controller endpoints.
 * This class contains meta-annotations that combine multiple Swagger/OpenAPI
 * annotations for cleaner controller code.
 */
public class ReactionControllerDocs {

    /**
     * Meta-annotation combining all OpenAPI documentation for the set reaction endpoint.
     * <p>
     * Apply this annotation to controller methods to include complete API documentation
     * for setting or updating a reaction on a post, including all request/response schemas and examples.
     * </p>
     */
    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @Operation(
            summary = "Set or update a reaction on a post",
            description = """
                    Sets or updates the authenticated user's reaction on a specific post.
                                
                    ## Requirements
                    - **Authentication**: Valid JWT access token required
                    - **Post ID**: Must be a valid post identifier that exists in the system
                    - **Reaction Type**: Must be either LIKE or DISLIKE
                                
                    ## Response
                    Returns the created or updated reaction details (200) upon success.
                                
                    ## Behavior
                    - If user has already reacted to the post, updates the reaction type
                    - If user hasn't reacted yet, creates a new reaction
                    - Users can change their reaction from LIKE to DISLIKE or vice versa
                    - If post doesn't exist, returns 404 Not Found
                    - If reaction type is invalid, returns 400 Bad Request
                    - If user is not authenticated, returns 401 Unauthorized
                    - If save fails due to database errors, returns 500 Internal Server Error
                    """,
            tags = {"Reactions"}
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Reaction successfully set or updated",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ReactionDTO.class),
                            examples = @ExampleObject(
                                    name = "Successful Reaction Set",
                                    summary = "Reaction created or updated on post",
                                    description = "Returns the reaction with all details",
                                    value = """
                                            {
                                              "id": 1,
                                              "reactionType": "LIKE",
                                              "userId": 7,
                                              "postId": 42
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
                                            name = "Reaction Type Null",
                                            summary = "Reaction type is null",
                                            value = """
                                                    {
                                                      "timestamp": "2026-02-13T14:39:00",
                                                      "status": 400,
                                                      "error": "Bad Request",
                                                      "message": "Validation Failed",
                                                      "path": "/posts/42/reactions",
                                                      "errors": [
                                                        "Reaction type must not be null"
                                                      ]
                                                    }
                                                    """
                                    ),
                                    @ExampleObject(
                                            name = "Invalid Reaction Type",
                                            summary = "Reaction type is not a valid enum value",
                                            value = """
                                                    {
                                                      "timestamp": "2026-02-13T14:39:00",
                                                      "status": 400,
                                                      "error": "Bad Request",
                                                      "message": "Invalid enum value",
                                                      "path": "/posts/42/reactions"
                                                    }
                                                    """
                                    ),
                                    @ExampleObject(
                                            name = "Missing Request Body",
                                            summary = "Request body is required but not provided",
                                            value = """
                                                    {
                                                      "timestamp": "2026-02-13T14:39:00",
                                                      "status": 400,
                                                      "error": "Bad Request",
                                                      "message": "Validation Failed",
                                                      "path": "/posts/42/reactions",
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
                    description = "Unauthorized - invalid or expired JWT token",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    name = "Invalid JWT Token",
                                    summary = "JWT token is invalid or has expired",
                                    value = """
                                            {
                                              "timestamp": "2026-02-13T14:39:00",
                                              "status": 401,
                                              "error": "Unauthorized",
                                              "message": "Invalid or expired JWT token",
                                              "path": "/posts/42/reactions"
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Forbidden - access denied",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    name = "Access Denied",
                                    summary = "User does not have permission to access this resource",
                                    value = """
                                            {
                                              "timestamp": "2026-02-13T14:39:00",
                                              "status": 403,
                                              "error": "Forbidden",
                                              "message": "Access Denied",
                                              "path": "/posts/42/reactions"
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Not Found - post does not exist",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    name = "Post Not Found",
                                    summary = "The specified post does not exist",
                                    value = """
                                            {
                                              "timestamp": "2026-02-13T14:39:00",
                                              "status": 404,
                                              "error": "Not Found",
                                              "message": "Post not found",
                                              "path": "/posts/999/reactions"
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error - failed to save reaction",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    name = "Save Failed",
                                    summary = "Reaction save failed due to database or system error",
                                    value = """
                                            {
                                              "timestamp": "2026-02-13T14:39:00",
                                              "status": 500,
                                              "error": "Internal Server Error",
                                              "message": "Failed to save reaction",
                                              "path": "/posts/42/reactions"
                                            }
                                            """
                            )
                    )
            )
    })
    public @interface SetReaction {
    }
}
