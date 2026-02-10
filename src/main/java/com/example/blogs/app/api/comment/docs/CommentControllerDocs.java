package com.example.blogs.app.api.comment.docs;

import com.example.blogs.app.api.comment.dto.CommentDTO;
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
 * OpenAPI documentation annotations for comment controller endpoints.
 * This class contains meta-annotations that combine multiple Swagger/OpenAPI
 * annotations for cleaner controller code.
 */
public class CommentControllerDocs {

    /**
     * Meta-annotation combining all OpenAPI documentation for the create comment endpoint.
     * <p>
     * Apply this annotation to controller methods to include complete API documentation
     * for creating a comment on a post, including all request/response schemas and examples.
     * </p>
     */
    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @Operation(
            summary = "Create a comment on a post",
            description = """
                    Creates a new comment on a specific post by the authenticated user.
                                
                    ## Requirements
                    - **Authentication**: Valid JWT access token required
                    - **Post ID**: Must be a valid post identifier that exists in the system
                    - **Content**: Comment content must not be blank
                                
                    ## Response
                    Returns the created comment details (201) upon success.
                                
                    ## Behavior
                    - Comment is associated with the authenticated user as the author
                    - Comment is associated with the specified post
                    - If post doesn't exist, returns appropriate error
                    - If content validation fails, returns 400 Bad Request
                    - If user is not authenticated, returns 401 Unauthorized
                    """,
            tags = {"Comments"}
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Comment successfully created",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = CommentDTO.class),
                            examples = @ExampleObject(
                                    name = "Successful Comment Creation",
                                    summary = "New comment created on post",
                                    description = "Returns the created comment with metadata",
                                    value = """
                                            {
                                              "id": 1,
                                              "content": "This is a great post! Thanks for sharing.",
                                              "postId": 42,
                                              "authorId": 7,
                                              "createdAt": "2026-02-10T00:30:00",
                                              "updatedAt": "2026-02-10T00:30:00",
                                              "edited": false
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
                                            name = "Content Blank",
                                            summary = "Comment content is blank or empty",
                                            value = """
                                                    {
                                                      "timestamp": "2026-02-10T00:30:00",
                                                      "status": 400,
                                                      "error": "Bad Request",
                                                      "message": "Validation Failed",
                                                      "path": "/posts/42/comment",
                                                      "errors": [
                                                        "Content must not be blank"
                                                      ]
                                                    }
                                                    """
                                    ),
                                    @ExampleObject(
                                            name = "Missing Request Body",
                                            summary = "Request body is required but not provided",
                                            value = """
                                                    {
                                                      "timestamp": "2026-02-10T00:30:00",
                                                      "status": 400,
                                                      "error": "Bad Request",
                                                      "message": "Validation Failed",
                                                      "path": "/posts/42/comment",
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
                                              "timestamp": "2026-02-10T00:30:00",
                                              "status": 401,
                                              "error": "Unauthorized",
                                              "message": "Invalid or expired JWT token",
                                              "path": "/posts/42/comment"
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
                                              "timestamp": "2026-02-10T00:30:00",
                                              "status": 403,
                                              "error": "Forbidden",
                                              "message": "Access Denied",
                                              "path": "/posts/42/comment"
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
                                              "timestamp": "2026-02-10T00:30:00",
                                              "status": 404,
                                              "error": "Not Found",
                                              "message": "Post not found",
                                              "path": "/posts/999/comment"
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error - failed to create comment",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    name = "Creation Failed",
                                    summary = "Comment creation failed due to database or system error",
                                    value = """
                                            {
                                              "timestamp": "2026-02-10T00:30:00",
                                              "status": 500,
                                              "error": "Internal Server Error",
                                              "message": "Failed to create comment",
                                              "path": "/posts/42/comment"
                                            }
                                            """
                            )
                    )
            )
    })
    public @interface CreateComment {
    }
}
