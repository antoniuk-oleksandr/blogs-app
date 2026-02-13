package com.example.blogs.app.api.reaction.service;

import com.example.blogs.app.api.post.entity.PostEntity;
import com.example.blogs.app.api.post.repository.adapter.PostRepositoryAdapter;
import com.example.blogs.app.api.reaction.dto.ReactionDTO;
import com.example.blogs.app.api.reaction.entity.ReactionEntity;
import com.example.blogs.app.api.reaction.entity.ReactionType;
import com.example.blogs.app.api.reaction.exception.ReactionNotFoundException;
import com.example.blogs.app.api.reaction.fixture.ReactionFixtures;
import com.example.blogs.app.api.reaction.mapper.ReactionMapper;
import com.example.blogs.app.api.reaction.repository.adapter.ReactionRepositoryAdapter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReactionServiceTest {

    @Mock
    private ReactionRepositoryAdapter reactionRepositoryAdapter;

    @Mock
    private PostRepositoryAdapter postRepositoryAdapter;

    @Mock
    private ReactionMapper reactionMapper;

    private ReactionService reactionService;

    @BeforeEach
    void setUp() {
        reactionService = new ReactionServiceImpl(
                reactionRepositoryAdapter,
                postRepositoryAdapter,
                reactionMapper
        );
    }

    @Test
    void setReaction_shouldUpdateExistingReaction_whenReactionExists() {
        Long postId = 1L;
        Long userId = 2L;
        Long reactionId = 1L;
        ReactionType newReactionType = ReactionType.DISLIKE;
        LocalDateTime now = LocalDateTime.now();

        ReactionEntity existingReaction = ReactionFixtures.reactionEntity(
                reactionId,
                now,
                null,
                null,
                ReactionType.LIKE
        );
        ReactionEntity updatedReaction = ReactionFixtures.reactionEntity(
                reactionId,
                now,
                null,
                null,
                ReactionType.DISLIKE
        );
        ReactionDTO mockDTO = ReactionFixtures.reactionDTO(
                reactionId,
                postId,
                userId,
                ReactionType.DISLIKE
        );

        when(reactionRepositoryAdapter.findByPostIdAndUserId(postId, userId))
                .thenReturn(existingReaction);
        when(reactionRepositoryAdapter.save(any(ReactionEntity.class)))
                .thenReturn(updatedReaction);
        when(reactionMapper.toReactionDTO(updatedReaction))
                .thenReturn(mockDTO);

        ReactionDTO result = reactionService.setReaction(postId, userId, newReactionType);

        assertThat(result).isEqualTo(mockDTO);
        assertThat(result.reactionType()).isEqualTo(ReactionType.DISLIKE);
        verify(reactionRepositoryAdapter).findByPostIdAndUserId(postId, userId);
        verify(reactionRepositoryAdapter).save(existingReaction);
        verify(reactionMapper).toReactionDTO(updatedReaction);
        verify(postRepositoryAdapter, never()).findById(anyLong());
    }

    @Test
    void setReaction_shouldCreateNewReaction_whenReactionDoesNotExist() {
        Long postId = 1L;
        Long userId = 2L;
        Long reactionId = 1L;
        ReactionType reactionType = ReactionType.LIKE;
        LocalDateTime now = LocalDateTime.now();

        PostEntity mockPost = PostEntity.builder()
                .id(postId)
                .build();
        ReactionEntity createdReaction = ReactionFixtures.reactionEntity(
                reactionId, now, null, null, ReactionType.LIKE
        );
        ReactionDTO mockDTO = ReactionFixtures.reactionDTO(
                reactionId, postId, userId, ReactionType.LIKE
        );

        when(reactionRepositoryAdapter.findByPostIdAndUserId(postId, userId))
                .thenThrow(new ReactionNotFoundException(null));
        when(postRepositoryAdapter.findById(postId))
                .thenReturn(mockPost);
        when(reactionRepositoryAdapter.save(any(ReactionEntity.class)))
                .thenReturn(createdReaction);
        when(reactionMapper.toReactionDTO(createdReaction))
                .thenReturn(mockDTO);

        ReactionDTO result = reactionService.setReaction(postId, userId, reactionType);

        assertThat(result).isEqualTo(mockDTO);
        assertThat(result.reactionType()).isEqualTo(ReactionType.LIKE);
        verify(reactionRepositoryAdapter).findByPostIdAndUserId(postId, userId);
        verify(postRepositoryAdapter).findById(postId);
        verify(reactionRepositoryAdapter).save(any(ReactionEntity.class));
        verify(reactionMapper).toReactionDTO(createdReaction);
    }

    @Test
    void setReaction_shouldChangeReactionType_whenChangingFromLikeToDislike() {
        Long postId = 1L;
        Long userId = 2L;
        Long reactionId = 1L;
        LocalDateTime now = LocalDateTime.now();

        ReactionEntity existingReaction = ReactionFixtures.reactionEntity(
                reactionId,
                now,
                null,
                null,
                ReactionType.LIKE
        );
        ReactionEntity updatedReaction = ReactionFixtures.reactionEntity(
                reactionId,
                now,
                null,
                null,
                ReactionType.DISLIKE
        );
        ReactionDTO mockDTO = ReactionFixtures.reactionDTO(
                reactionId,
                postId,
                userId,
                ReactionType.DISLIKE
        );

        when(reactionRepositoryAdapter.findByPostIdAndUserId(postId, userId))
                .thenReturn(existingReaction);
        when(reactionRepositoryAdapter.save(any(ReactionEntity.class)))
                .thenReturn(updatedReaction);
        when(reactionMapper.toReactionDTO(updatedReaction))
                .thenReturn(mockDTO);

        ReactionDTO result = reactionService.setReaction(postId, userId, ReactionType.DISLIKE);

        assertThat(result.reactionType()).isEqualTo(ReactionType.DISLIKE);
        verify(reactionRepositoryAdapter).save(existingReaction);
    }
}
