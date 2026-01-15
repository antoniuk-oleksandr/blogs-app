package com.example.blogs.app.api.post.controller;

import com.example.blogs.app.api.post.docs.PostControllerDocs;
import com.example.blogs.app.api.post.dto.PostDTO;
import com.example.blogs.app.api.post.service.PostService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    public ResponseEntity<PostDTO> getPostBySlug(@PathVariable String slug) {
        PostDTO postDTO = postService.getPostBySlug(slug);
        return ResponseEntity.ok(postDTO);
    }
}
