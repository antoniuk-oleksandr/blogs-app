package com.example.blogs.app.api.reaction.repository;

import com.example.blogs.app.api.reaction.entity.ReactionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ReactionRepository extends JpaRepository<ReactionEntity, Long> {

    Optional<ReactionEntity> findByPostIdAndUserId(Long postId, Long userId);
}
