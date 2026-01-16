package com.example.blogs.app.api.post.service;

import com.example.blogs.app.api.comment.entity.CommentEntity;
import com.example.blogs.app.api.comment.service.CommentService;
import com.example.blogs.app.api.post.dto.PostDTO;
import com.example.blogs.app.api.post.entity.PostEntity;
import com.example.blogs.app.api.post.mapper.PostMapper;
import com.example.blogs.app.api.post.repository.adapter.PostRepositoryAdapter;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Orchestrates post retrieval operations by coordinating with the post repository adapter.
 */
@Service
@AllArgsConstructor
public class PostServiceImpl implements PostService {

    private final PostRepositoryAdapter postRepositoryAdapter;

    private final CommentService commentService;

    private final PostMapper postMapper;

    /**
     * Retrieves all posts created by the specified user.
     * Delegates to the repository adapter for data retrieval.
     *
     * @param userId the ID of the user
     * @return list of posts created by the user
     */
    @Override
    public List<PostEntity> getPostsByUserId(long userId) {
        return postRepositoryAdapter.findByAuthorId(userId);
    }

    @Override
    public void deletePostById(Long postId) {
        postRepositoryAdapter.deleteById(postId);
    }

    /**
     * Retrieves a post by its unique slug identifier with associated comments.
     * Delegates to the repository adapter for post retrieval and comment service for comments.
     *
     * @param slug the unique slug of the post
     * @return post details with associated comments
     */
    @Override
    public PostDTO getPostBySlug(String slug) {
        PostEntity post = postRepositoryAdapter.findBySlug(slug);
        List<CommentEntity> comments = commentService.getCommentsByPostId(post.getId());

        return postMapper.toPostDTO(post, comments);
    }
}
