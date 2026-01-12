package com.example.blogs.app.api.post.repository;

import com.example.blogs.app.api.post.entity.PostEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PostRepository extends JpaRepository<PostEntity, Long> {
    List<PostEntity> findByAuthorId(long userId);
}
