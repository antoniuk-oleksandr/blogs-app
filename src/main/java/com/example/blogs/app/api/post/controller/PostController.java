package com.example.blogs.app.api.post.controller;

import com.example.blogs.app.api.post.docs.PostControllerDocs;
import com.example.blogs.app.api.post.dto.*;
import com.example.blogs.app.api.post.service.PostService;
import com.example.blogs.app.security.UserPrincipal;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * REST controller for managing post resources.
 */
@RestController
@AllArgsConstructor
@RequestMapping("/posts")
public class PostController {

    private final PostService postService;

    /**
     * Deletes a post by its ID.
     *
     * @param postId the ID of the post to delete
     * @return no content response on successful deletion
     */
    @DeleteMapping("/{postId}")
    @PreAuthorize("@postSecurity.isOwner(#postId)")
    @PostControllerDocs.DeletePostById
    public ResponseEntity<Void> deletePostById(@PathVariable Long postId) {
        postService.deletePostById(postId);

        return ResponseEntity.noContent().build();
    }

    /**
     * Retrieves a post by its unique slug identifier.
     *
     * @param slug the unique slug of the post
     * @return post details with associated comments
     */
    @GetMapping("/{slug}")
    @PostControllerDocs.GetPostBySlug
    public ResponseEntity<PostDTO> getPostBySlug(@PathVariable String slug) {
        PostDTO postDTO = postService.getPostBySlug(slug);
        return ResponseEntity.ok(postDTO);
    }

    /**
     * Updates a post by its ID with partial field updates.
     * At least one field must be provided in the request.
     *
     * @param postId     the ID of the post to update
     * @param requestDTO the update request containing fields to update
     * @return updated post details
     */
    @PatchMapping("/{postId}")
    @PostControllerDocs.UpdatePostById
    @PreAuthorize("@postSecurity.isOwner(#postId)")
    public ResponseEntity<PostUpdateResponseDTO> updatePostById(
            @PathVariable Long postId,
            @NotNull @Valid @RequestBody PostUpdateRequestDTO requestDTO
    ) {
        return ResponseEntity
                .ok(postService.updatePostById(postId, requestDTO));
    }

    /**
     * Creates a new post with a preview image.
     *
     * @param userPrincipal authenticated user principal containing the author ID
     * @param requestDTO    the post creation request containing title, description, and content
     * @param previewImage  the preview image file to upload
     * @return created post details with generated slug and preview image URL
     */
    @PostControllerDocs.CreatePost
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<PostCreateResponseDTO> createPost(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @NotNull @Valid @RequestPart(value = "post") PostCreateRequestDTO requestDTO,
            @RequestPart(value = "previewImage") MultipartFile previewImage
    ) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(postService.createPost(userPrincipal.id(), requestDTO, previewImage));
    }
}
