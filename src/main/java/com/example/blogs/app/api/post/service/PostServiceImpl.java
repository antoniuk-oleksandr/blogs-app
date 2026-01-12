package com.example.blogs.app.api.post.service;

import com.example.blogs.app.api.post.entity.PostEntity;
import com.example.blogs.app.api.post.repository.adapter.PostRepositoryAdapter;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class PostServiceImpl implements PostService {

    private final PostRepositoryAdapter postRepositoryAdapter;

    @Override
    public List<PostEntity> getPostsByUserId(long userId) {
        return postRepositoryAdapter.findByAuthorId(userId);
    }
}
