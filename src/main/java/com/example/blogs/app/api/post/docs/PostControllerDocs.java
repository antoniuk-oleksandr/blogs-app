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
                                              "timestamp": "2024-12-22T02:36:59.123456",
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
                                              "timestamp": "2024-12-22T02:36:59.123456",
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
}
