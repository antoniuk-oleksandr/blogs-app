package com.example.blogs.app.api.post.service;

import com.example.blogs.app.api.post.entity.PostEntity;
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
}
