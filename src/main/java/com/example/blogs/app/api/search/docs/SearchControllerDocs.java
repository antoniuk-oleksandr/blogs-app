package com.example.blogs.app.api.search.docs;

import com.example.blogs.app.api.search.dto.SearchPostsResponseDTO;
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
 * OpenAPI documentation annotations for search controller endpoints.
 */
public class SearchControllerDocs {

    /**
     * Meta-annotation combining OpenAPI documentation for searching posts.
     */
    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @Operation(
            summary = "Search posts",
            description = """
                    Searches indexed posts by title, description, and content.

                    ## Query Parameters
                    - **query**: Search text
                    - **type**: Sorting/search mode. Allowed values: RELEVANCE, NEWEST, OLDEST
                    - **cursor**: Optional cursor returned by the previous response

                    ## Response
                    Returns a page of posts with cursor metadata.
                    """,
            tags = {"Search"}
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Posts successfully searched",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = SearchPostsResponseDTO.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid request parameters or cursor",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    name = "Invalid Cursor",
                                    value = """
                                            {
                                              "timestamp": "2026-07-15T12:00:00",
                                              "status": 400,
                                              "error": "Bad Request",
                                              "message": "Invalid cursor provided",
                                              "path": "/search"
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Search failed",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    name = "Search Failed",
                                    value = """
                                            {
                                              "timestamp": "2026-07-15T12:00:00",
                                              "status": 500,
                                              "error": "Internal Server Error",
                                              "message": "Failed to search posts",
                                              "path": "/search"
                                            }
                                            """
                            )
                    )
            )
    })
    public @interface SearchPosts {
    }
}
