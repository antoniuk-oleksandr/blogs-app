package com.example.blogs.app.api.comment.controller;

import com.example.blogs.app.api.comment.docs.CommentControllerDocs;
import com.example.blogs.app.api.comment.dto.CommentWriteRequestDTO;
import com.example.blogs.app.api.comment.dto.CommentDTO;
import com.example.blogs.app.api.comment.service.CommentService;
import com.example.blogs.app.security.UserPrincipal;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

/**
 * Handles HTTP requests for comment-related operations.
 */
@RestController
@AllArgsConstructor
public class CommentController {

    private final CommentService commentService;

    /**
     * Creates a new comment on a specific post.
     * Validates the request and delegates to the service layer for business logic.
     *
     * @param userPrincipal authenticated user making the comment
     * @param postId        the ID of the post to comment on
     * @param requestDTO    request containing the comment content
     * @return newly created comment with HTTP 201 status
     */
    @CommentControllerDocs.CreateComment
    @PostMapping("/posts/{postId}/comment")
    public ResponseEntity<CommentDTO> createComment(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @PathVariable Long postId,
            @NotNull @Valid @RequestBody CommentWriteRequestDTO requestDTO
    ) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(commentService.createComment(postId, userPrincipal.id(), requestDTO));
    }

    /**
     * Deletes a comment by its ID.
     * Requires the authenticated user to be the owner of the comment.
     *
     * @param commentId the ID of the comment to delete
     * @return HTTP 204 No Content status upon successful deletion
     */
    @CommentControllerDocs.DeleteCommentById
    @DeleteMapping("/comments/{commentId}")
    @PreAuthorize("@commentSecurity.isOwner(#commentId)")
    public ResponseEntity<Void> deleteCommentById(@PathVariable Long commentId) {
        commentService.deleteCommentById(commentId);

        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/comments/{commentId}")
    @PreAuthorize("@commentSecurity.isOwner(#commentId)")
    public ResponseEntity<CommentDTO> updateCommentById(
            @PathVariable Long commentId,
            @NotNull @Valid @RequestBody CommentWriteRequestDTO requestDTO
    ) {
        return ResponseEntity
                .ok(commentService.updateCommentById(commentId, requestDTO));
    }
}
