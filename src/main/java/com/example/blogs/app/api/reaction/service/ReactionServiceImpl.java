package com.example.blogs.app.api.reaction.service;

import com.example.blogs.app.api.post.entity.PostEntity;
import com.example.blogs.app.api.post.repository.adapter.PostRepositoryAdapter;
import com.example.blogs.app.api.reaction.dto.ReactionDTO;
import com.example.blogs.app.api.reaction.entity.ReactionEntity;
import com.example.blogs.app.api.reaction.entity.ReactionType;
import com.example.blogs.app.api.reaction.exception.ReactionNotFoundException;
import com.example.blogs.app.api.reaction.mapper.ReactionMapper;
import com.example.blogs.app.api.reaction.repository.adapter.ReactionRepositoryAdapter;
import com.example.blogs.app.api.user.entity.UserEntity;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Orchestrates reaction operations by coordinating with repository adapters.
 */
@Service
@AllArgsConstructor
public class ReactionServiceImpl implements ReactionService {

    private final ReactionRepositoryAdapter reactionRepositoryAdapter;

    private final PostRepositoryAdapter postRepositoryAdapter;

    private final ReactionMapper reactionMapper;

    /**
     * Sets or updates a user's reaction on a post.
     * Attempts to find an existing reaction and update it. If not found, creates a new reaction.
     *
     * @param postId the ID of the post to react to
     * @param userId the ID of the user making the reaction
     * @param reactionType the type of reaction (LIKE or DISLIKE)
     * @return the created or updated reaction as DTO
     */
    @Override
    public ReactionDTO setReaction(Long postId, Long userId, ReactionType reactionType) {
        try {
            ReactionEntity existing =
                    reactionRepositoryAdapter.findByPostIdAndUserId(postId, userId);

            existing.setReactionType(reactionType);
            return reactionMapper.toReactionDTO(
                    reactionRepositoryAdapter.save(existing)
            );

        } catch (ReactionNotFoundException ignored) {
            PostEntity post = postRepositoryAdapter.findById(postId);

            UserEntity user = UserEntity.builder()
                    .id(userId)
                    .build();

            ReactionEntity created = ReactionEntity.builder()
                    .post(post)
                    .user(user)
                    .reactionType(reactionType)
                    .build();

            return reactionMapper.toReactionDTO(
                    reactionRepositoryAdapter.save(created)
            );
        }
    }
}
