package com.example.blogs.app.api.reaction.repository;

import com.example.blogs.app.api.post.entity.PostEntity;
import com.example.blogs.app.api.post.fixture.PostFixtures;
import com.example.blogs.app.api.post.repository.PostRepository;
import com.example.blogs.app.api.reaction.entity.ReactionEntity;
import com.example.blogs.app.api.reaction.entity.ReactionType;
import com.example.blogs.app.api.reaction.fixture.ReactionFixtures;
import com.example.blogs.app.api.user.entity.UserEntity;
import com.example.blogs.app.api.user.fixture.UserFixtures;
import com.example.blogs.app.api.user.repository.UserRepository;
import com.example.blogs.app.support.AbstractPostgresTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

/**
 * Integration tests for reaction repository database operations.
 */
@DataJpaTest
class ReactionRepositoryTest extends AbstractPostgresTest {

    @Autowired
    private ReactionRepository reactionRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PostRepository postRepository;

    @Test
    void findByPostIdAndUserId_shouldReturnReaction_whenReactionExists() {
        UserEntity user = UserFixtures.user();
        UserEntity createdUser = userRepository.save(user);

        PostEntity post = PostFixtures.post(createdUser);
        PostEntity createdPost = postRepository.save(post);

        ReactionEntity reaction = ReactionFixtures.reactionEntity(
                createdUser, createdPost, ReactionType.LIKE
        );
        ReactionEntity createdReaction = reactionRepository.save(reaction);

        Optional<ReactionEntity> foundReaction = reactionRepository.findByPostIdAndUserId(
                createdPost.getId(), createdUser.getId()
        );

        assertThat(foundReaction).isPresent();
        assertThat(foundReaction.get().getId()).isEqualTo(createdReaction.getId());
        assertThat(foundReaction.get().getReactionType()).isEqualTo(ReactionType.LIKE);
        assertThat(foundReaction.get().getUser().getId()).isEqualTo(createdUser.getId());
        assertThat(foundReaction.get().getPost().getId()).isEqualTo(createdPost.getId());
    }

    @Test
    void findByPostIdAndUserId_shouldReturnEmpty_whenReactionDoesNotExist() {
        Optional<ReactionEntity> foundReaction = reactionRepository.findByPostIdAndUserId(9999L, 9999L);

        assertThat(foundReaction).isEmpty();
    }

    @Test
    void save_shouldCreateReaction() {
        UserEntity user = UserFixtures.user();
        UserEntity createdUser = userRepository.save(user);

        PostEntity post = PostFixtures.post(createdUser);
        PostEntity createdPost = postRepository.save(post);

        ReactionEntity reaction = ReactionFixtures.reactionEntity(
                createdUser, createdPost, ReactionType.DISLIKE
        );

        ReactionEntity savedReaction = reactionRepository.save(reaction);

        assertThat(savedReaction.getId()).isNotNull();
        assertThat(savedReaction.getReactionType()).isEqualTo(ReactionType.DISLIKE);
        assertThat(savedReaction.getUser().getId()).isEqualTo(createdUser.getId());
        assertThat(savedReaction.getPost().getId()).isEqualTo(createdPost.getId());
        assertThat(savedReaction.getCreatedAt()).isNotNull();
    }

    @Test
    void save_shouldUpdateReaction() {
        UserEntity user = UserFixtures.user();
        UserEntity createdUser = userRepository.save(user);

        PostEntity post = PostFixtures.post(createdUser);
        PostEntity createdPost = postRepository.save(post);

        ReactionEntity reaction = ReactionFixtures.reactionEntity(
                createdUser, createdPost, ReactionType.LIKE
        );
        ReactionEntity savedReaction = reactionRepository.save(reaction);

        savedReaction.setReactionType(ReactionType.DISLIKE);
        ReactionEntity updatedReaction = reactionRepository.save(savedReaction);

        assertThat(updatedReaction.getId()).isEqualTo(savedReaction.getId());
        assertThat(updatedReaction.getReactionType()).isEqualTo(ReactionType.DISLIKE);
    }
}
