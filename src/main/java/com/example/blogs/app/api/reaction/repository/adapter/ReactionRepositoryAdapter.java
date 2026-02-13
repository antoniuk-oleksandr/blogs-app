package com.example.blogs.app.api.reaction.repository.adapter;

import com.example.blogs.app.api.reaction.entity.ReactionEntity;

public interface ReactionRepositoryAdapter {

     ReactionEntity save(ReactionEntity reactionEntity);

     ReactionEntity findByPostIdAndUserId(Long postId, Long userId);
}
