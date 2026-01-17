package com.example.blogs.app.api.post.docs;

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
 * OpenAPI documentation annotations for post controller endpoints.
 * This class contains meta-annotations that combine multiple Swagger/OpenAPI
 * annotations for cleaner controller code.
 */
public class PostControllerDocs {

    /**
     * Meta-annotation combining all OpenAPI documentation for the delete post by ID endpoint.
     * <p>
     * Apply this annotation to controller methods to include complete API documentation
     * for deleting a post, including all response schemas and examples.
     * </p>
     */
    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @Operation(
            summary = "Delete a post by ID",
            description = """
                    Deletes a specific post from the system by its unique identifier.
                                
                    ## Requirements
                    - **Post ID**: Must be a valid post identifier that exists in the system
                                
                    ## Response
                    Returns no content (204) upon successful deletion.
                                
                    ## Behavior
                    - Post is permanently removed from the database
                    - If post doesn't exist, returns 404 Not Found
                    - If deletion fails due to database errors, returns 500 Internal Server Error
                    """,
            tags = {"Posts"}
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "Post successfully deleted",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)
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
                                              "timestamp": "2026-01-16T16:15:06",
                                              "status": 401,
                                              "error": "Unauthorized",
                                              "message": "Invalid or expired JWT token",
                                              "path": "/posts/7"
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
                                              "timestamp": "2026-01-16T16:16:26",
                                              "status": 403,
                                              "error": "Forbidden",
                                              "message": "Access Denied",
                                              "path": "/posts/11"
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
                                    summary = "The requested post does not exist",
                                    value = """
                                            {
                                              "timestamp": "2026-01-16T16:16:26",
                                              "status": 404,
                                              "error": "Not Found",
                                              "message": "Post not found",
                                              "path": "/posts/999"
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error - failed to delete post",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    name = "Deletion Failed",
                                    summary = "Post deletion failed due to database or system error",
                                    value = """
                                            {
                                              "timestamp": "2026-01-16T16:16:26",
                                              "status": 500,
                                              "error": "Internal Server Error",
                                              "message": "Failed to delete post",
                                              "path": "/posts/123"
                                            }
                                            """
                            )
                    )
            )
    })
    public @interface DeletePostById {
    }

    /**
     * Meta-annotation combining all OpenAPI documentation for the get post by slug endpoint.
     * <p>
     * Apply this annotation to controller methods to include complete API documentation
     * for retrieving a post by its slug, including all response schemas and examples.
     * </p>
     */
    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @Operation(
            summary = "Get a post by slug",
            description = """
                    Retrieves a specific post from the system by its unique slug identifier,
                    including all associated comments.
                                
                    ## Requirements
                    - **Slug**: Must be a valid post slug that exists in the system
                                
                    ## Response
                    Returns the post details with associated comments (200) upon success.
                                
                    ## Behavior
                    - Post and its comments are retrieved from the database
                    - If post doesn't exist, returns 404 Not Found
                    - If retrieval fails due to database errors, returns 500 Internal Server Error
                    """,
            tags = {"Posts"}
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Post successfully retrieved",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Not Found - post does not exist",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    name = "Post Not Found",
                                    summary = "The requested post does not exist",
                                    value = """
                                            {
                                              "timestamp": "2026-01-16T16:16:26",
                                              "status": 404,
                                              "error": "Not Found",
                                              "message": "Post not found",
                                              "path": "/posts/my-post-slug"
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error - failed to find post by slug",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    name = "Retrieval Failed",
                                    summary = "Post retrieval failed due to database or system error",
                                    value = """
                                            {
                                              "timestamp": "2026-01-16T16:16:26",
                                              "status": 500,
                                              "error": "Internal Server Error",
                                              "message": "Failed to find post by slug",
                                              "path": "/posts/my-post-slug"
                                            }
                                            """
                            )
                    )
            )
    })
    public @interface GetPostBySlug {
    }

    /**
     * Meta-annotation combining all OpenAPI documentation for the update post by ID endpoint.
     * <p>
     * Apply this annotation to controller methods to include complete API documentation
     * for updating a post, including all request/response schemas and examples.
     * </p>
     */
    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @Operation(
            summary = "Update a post by ID",
            description = """
                    Updates a specific post with partial field updates by its unique identifier.
                                
                    ## Requirements
                    - **Post ID**: Must be a valid post identifier that exists in the system
                    - **At least one field**: Must provide at least one field to update (title, description, or content)
                                
                    ## Response
                    Returns the updated post details (200) upon success.
                                
                    ## Behavior
                    - Only provided fields are updated; null fields are ignored
                    - Slug is automatically regenerated if title is updated
                    - If post doesn't exist, returns 404 Not Found
                    - If no fields provided, returns 400 Bad Request
                    - If update fails due to database errors, returns 500 Internal Server Error
                    """,
            tags = {"Posts"}
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Post successfully updated",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = com.example.blogs.app.api.post.dto.PostUpdateResponseDTO.class),
                            examples = @ExampleObject(
                                    name = "Successful Update",
                                    summary = "Post successfully updated with new values",
                                    description = "Returns updated post with all current field values",
                                    value = """
                                            {
                                              "id": 1,
                                              "title": "Updated Title",
                                              "description": "Updated description",
                                              "content": "Updated content with more details",
                                              "slug": "updated-title",
                                              "previewImageUrl": "https://example.com/image.jpg",
                                              "updatedAt": "2026-01-17T00:45:00"
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
                                            name = "No Fields Provided",
                                            summary = "At least one field must be provided for update",
                                            value = """
                                                    {
                                                      "timestamp": "2026-01-17T00:45:00",
                                                      "status": 400,
                                                      "error": "Bad Request",
                                                      "message": "Validation Failed",
                                                      "path": "/posts/1",
                                                      "errors": [
                                                        "At least one field must be provided"
                                                      ]
                                                    }
                                                    """
                                    ),
                                    @ExampleObject(
                                            name = "Missing Request Body",
                                            summary = "Request body is required but not provided",
                                            value = """
                                                    {
                                                      "timestamp": "2026-01-17T00:45:00",
                                                      "status": 400,
                                                      "error": "Bad Request",
                                                      "message": "Validation Failed",
                                                      "path": "/posts/1",
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
                                              "timestamp": "2026-01-17T00:45:00",
                                              "status": 401,
                                              "error": "Unauthorized",
                                              "message": "Invalid or expired JWT token",
                                              "path": "/posts/1"
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
                                    summary = "User does not have permission to update this post",
                                    value = """
                                            {
                                              "timestamp": "2026-01-17T00:45:00",
                                              "status": 403,
                                              "error": "Forbidden",
                                              "message": "Access Denied",
                                              "path": "/posts/1"
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
                                    summary = "The requested post does not exist",
                                    value = """
                                            {
                                              "timestamp": "2026-01-17T00:45:00",
                                              "status": 404,
                                              "error": "Not Found",
                                              "message": "Post not found",
                                              "path": "/posts/999"
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error - failed to update post",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    name = "Update Failed",
                                    summary = "Post update failed due to database or system error",
                                    value = """
                                            {
                                              "timestamp": "2026-01-17T00:45:00",
                                              "status": 500,
                                              "error": "Internal Server Error",
                                              "message": "Failed to update post",
                                              "path": "/posts/1"
                                            }
                                            """
                            )
                    )
            )
    })
    public @interface UpdatePostById {
    }
}
