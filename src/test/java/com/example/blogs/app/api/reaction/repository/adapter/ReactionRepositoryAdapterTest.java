package com.example.blogs.app.api.reaction.repository.adapter;

import com.example.blogs.app.api.reaction.entity.ReactionEntity;
import com.example.blogs.app.api.reaction.entity.ReactionType;
import com.example.blogs.app.api.reaction.exception.FailedToFindReactionException;
import com.example.blogs.app.api.reaction.exception.FailedToSaveReactionException;
import com.example.blogs.app.api.reaction.exception.ReactionNotFoundException;
import com.example.blogs.app.api.reaction.exception.UserHasAlreadyReactedException;
import com.example.blogs.app.api.reaction.fixture.ReactionFixtures;
import com.example.blogs.app.api.reaction.repository.ReactionRepository;
import com.example.blogs.app.util.SqlExceptionUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReactionRepositoryAdapterTest {

    @Mock
    private ReactionRepository reactionRepository;

    @Mock
    private SqlExceptionUtils sqlExceptionUtils;

    private ReactionRepositoryAdapter reactionRepositoryAdapter;

    @BeforeEach
    void setUp() {
        reactionRepositoryAdapter = new ReactionRepositoryAdapterImpl(
                reactionRepository,
                sqlExceptionUtils
        );
    }

    @Test
    void save_shouldReturnSavedReaction() {
        ReactionEntity mockReaction = ReactionFixtures.reactionEntity(
                null,
                null,
                ReactionType.LIKE
        );
        when(reactionRepository.save(any(ReactionEntity.class))).thenReturn(mockReaction);

        ReactionEntity result = reactionRepositoryAdapter.save(mockReaction);

        assertThat(result).isEqualTo(mockReaction);
        verify(reactionRepository).save(mockReaction);
    }

    @Test
    void save_shouldThrowFailedToSaveReactionException_whenDatabaseFails() {
        ReactionEntity mockReaction = ReactionFixtures.reactionEntity(
                null,
                null,
                ReactionType.LIKE
        );
        RuntimeException dbException = new RuntimeException("Database error");
        when(reactionRepository.save(any(ReactionEntity.class))).thenThrow(dbException);
        when(sqlExceptionUtils.containsUniqueViolation(any(), anyString())).thenReturn(false);

        assertThatThrownBy(() -> reactionRepositoryAdapter.save(mockReaction))
                .isInstanceOf(FailedToSaveReactionException.class)
                .hasMessage("Failed to save reaction");

        verify(reactionRepository).save(mockReaction);
    }

    @Test
    void save_shouldThrowUserHasAlreadyReactedException_whenUniqueViolationOccurs() {
        ReactionEntity mockReaction = ReactionFixtures.reactionEntity(
                null,
                null,
                ReactionType.LIKE
        );
        RuntimeException dbException = new RuntimeException("Database error");
        when(reactionRepository.save(any(ReactionEntity.class))).thenThrow(dbException);
        when(sqlExceptionUtils.containsUniqueViolation(dbException, "user_id")).thenReturn(true);

        assertThatThrownBy(() -> reactionRepositoryAdapter.save(mockReaction))
                .isInstanceOf(UserHasAlreadyReactedException.class)
                .hasMessage("User has already reacted");

        verify(reactionRepository).save(mockReaction);
    }

    @Test
    void findByPostIdAndUserId_shouldReturnReaction_whenReactionExists() {
        Long postId = 1L;
        Long userId = 2L;
        ReactionEntity mockReaction = ReactionFixtures.reactionEntity(
                1L,
                LocalDateTime.now(),
                null,
                null,
                ReactionType.LIKE
        );
        when(reactionRepository.findByPostIdAndUserId(postId, userId))
                .thenReturn(Optional.of(mockReaction));

        ReactionEntity result = reactionRepositoryAdapter.findByPostIdAndUserId(postId, userId);

        assertThat(result).isEqualTo(mockReaction);
        verify(reactionRepository).findByPostIdAndUserId(postId, userId);
    }

    @Test
    void findByPostIdAndUserId_shouldThrowReactionNotFoundException_whenReactionDoesNotExist() {
        Long postId = 1L;
        Long userId = 2L;
        when(reactionRepository.findByPostIdAndUserId(postId, userId))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> reactionRepositoryAdapter.findByPostIdAndUserId(postId, userId))
                .isInstanceOf(ReactionNotFoundException.class)
                .hasMessage("Reaction not found");

        verify(reactionRepository).findByPostIdAndUserId(postId, userId);
    }

    @Test
    void findByPostIdAndUserId_shouldThrowFailedToFindReactionException_whenDatabaseFails() {
        Long postId = 1L;
        Long userId = 2L;
        when(reactionRepository.findByPostIdAndUserId(postId, userId))
                .thenThrow(new RuntimeException("Database error"));

        assertThatThrownBy(() -> reactionRepositoryAdapter.findByPostIdAndUserId(postId, userId))
                .isInstanceOf(FailedToFindReactionException.class)
                .hasMessage("Failed to find reaction");

        verify(reactionRepository).findByPostIdAndUserId(postId, userId);
    }
}
