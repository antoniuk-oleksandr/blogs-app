package com.example.blogs.app.api.reaction.service;

import com.example.blogs.app.api.reaction.dto.ReactionDTO;
import com.example.blogs.app.api.reaction.entity.ReactionType;

public interface ReactionService {
    ReactionDTO setReaction(Long postId, Long userId, ReactionType reactionType);
}
