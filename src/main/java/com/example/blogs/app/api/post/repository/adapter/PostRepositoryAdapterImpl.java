package com.example.blogs.app.api.post.repository.adapter;

import com.example.blogs.app.api.post.entity.PostEntity;
import com.example.blogs.app.api.post.exception.FailedToFindPostsByAuthorIdException;
import com.example.blogs.app.api.post.repository.PostRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@AllArgsConstructor
public class PostRepositoryAdapterImpl implements PostRepositoryAdapter {

    private final PostRepository postRepository;

    @Override
    public List<PostEntity> findByAuthorId(long userId) {
        try {
            return postRepository.findByAuthorId(userId);
        } catch (Exception e) {
            throw new FailedToFindPostsByAuthorIdException(e);
        }
    }
}
