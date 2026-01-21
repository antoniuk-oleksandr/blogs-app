package com.example.blogs.app.api.post.service;

import com.example.blogs.app.api.post.dto.*;
import com.example.blogs.app.api.post.entity.PostEntity;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * Service for post-related business operations.
 */
public interface PostService {

    /**
     * Retrieves all posts created by the specified user.
     *
     * @param userId the ID of the user
     * @return list of posts created by the user
     */
    List<PostEntity> getPostsByUserId(long userId);

    /**
     * Deletes a post by its ID.
     *
     * @param postId the ID of the post to delete
     */
    void deletePostById(Long postId);

    /**
     * Retrieves a post by its unique slug identifier with associated comments.
     *
     * @param slug the unique slug of the post
     * @return post details with associated comments
     */
    PostDTO getPostBySlug(String slug);

    /**
     * Updates a post by its ID with partial field updates.
     * If the title is updated, a new slug is generated.
     *
     * @param postId     the ID of the post to update
     * @param requestDTO the update request containing fields to update
     * @return updated post details with new timestamp
     */
    PostUpdateResponseDTO updatePostById(long postId, PostUpdateRequestDTO requestDTO);

    PostCreateResponseDTO createPost(long authorId, PostCreateRequestDTO requestDTO, MultipartFile previewImage);
}
