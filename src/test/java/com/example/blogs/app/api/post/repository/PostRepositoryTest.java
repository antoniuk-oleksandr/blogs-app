package com.example.blogs.app.api.post.repository;

import com.example.blogs.app.api.post.entity.PostEntity;
import com.example.blogs.app.api.post.fixture.PostFixtures;
import com.example.blogs.app.api.user.entity.UserEntity;
import com.example.blogs.app.api.user.fixture.UserFixtures;
import com.example.blogs.app.api.user.repository.UserRepository;
import com.example.blogs.app.support.AbstractPostgresTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

@DataJpaTest
class PostRepositoryTest extends AbstractPostgresTest {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    void findByAuthorId_shouldReturnPosts_whenAuthorIdExists() {
        UserEntity user = UserFixtures.user();
        PostEntity post = PostFixtures.post(user);
        UserEntity createdUser = userRepository.save(user);
        postRepository.save(post);

        List<PostEntity> foundPosts = postRepository.findByAuthorId(createdUser.getId());

        assertThat(foundPosts).isNotEmpty();
        assertThat(foundPosts.getFirst().getId()).isPositive();
        assertThat(foundPosts.getFirst().getTitle()).isEqualTo(post.getTitle());
        assertThat(foundPosts.getFirst().getSlug()).isEqualTo(post.getSlug());
        assertThat(foundPosts.getFirst().getPreviewImageUrl()).isEqualTo(post.getPreviewImageUrl());
        assertThat(foundPosts.getFirst().getDescription()).isEqualTo(post.getDescription());
        assertThat(foundPosts.getFirst().getContent()).isEqualTo(post.getContent());
        assertThat(foundPosts.getFirst().getAuthor().getId()).isEqualTo(createdUser.getId());
    }

    @Test
    void deleteByIdReturningCount_shouldReturnCount_whenPostIdExists() {
        UserEntity user = UserFixtures.user();
        UserEntity createdUser = userRepository.save(user);
        PostEntity post = PostFixtures.post(createdUser);
        PostEntity createdPost = postRepository.save(post);

        Long deletedCount = postRepository.deleteByIdReturningCount(createdPost.getId());

        assertThat(deletedCount).isEqualTo(createdPost.getId());
    }

    @Test
    void deleteByIdReturningCount_shouldReturnNull_whenPostIdDoesNotExist() {
        Long deletedCount = postRepository.deleteByIdReturningCount(9999L);

        assertThat(deletedCount).isNull();
    }

    @Test
    void findBySlug_shouldReturnPost_whenSlugExists() {
        UserEntity user = UserFixtures.user();
        UserEntity createdUser = userRepository.save(user);
        PostEntity post = PostFixtures.post(createdUser);
        PostEntity createdPost = postRepository.save(post);

        Optional<PostEntity> foundPostOpt = postRepository.findBySlug("slug");

        assertThat(foundPostOpt).isPresent();
        PostEntity foundPost = foundPostOpt.get();
        assertThat(foundPost.getId()).isEqualTo(createdPost.getId());
        assertThat(foundPost.getTitle()).isEqualTo(post.getTitle());
        assertThat(foundPost.getSlug()).isEqualTo(post.getSlug());
        assertThat(foundPost.getPreviewImageUrl()).isEqualTo(post.getPreviewImageUrl());
        assertThat(foundPost.getDescription()).isEqualTo(post.getDescription());
        assertThat(foundPost.getContent()).isEqualTo(post.getContent());
        assertThat(foundPost.getAuthor().getId()).isEqualTo(createdUser.getId());
    }

    @Test
    void existsById_shouldReturnTrue_whenPostIdExists() {
        UserEntity user = UserFixtures.user();
        UserEntity createdUser = userRepository.save(user);
        PostEntity post = PostFixtures.post(createdUser);
        PostEntity createdPost = postRepository.save(post);

        boolean exists = postRepository.existsById(createdPost.getId());

        assertThat(exists).isTrue();
    }

    @Test
    void existsById_shouldReturnFalse_whenPostIdDoesNotExist() {
        boolean exists = postRepository.existsById(9999L);

        assertThat(exists).isFalse();
    }

    @Test
    void existsByIdAndAuthorId_shouldReturnTrue_whenPostIdAndAuthorIdExist() {
        UserEntity user = UserFixtures.user();
        UserEntity createdUser = userRepository.save(user);
        PostEntity post = PostFixtures.post(createdUser);
        PostEntity createdPost = postRepository.save(post);

        boolean exists = postRepository.existsByIdAndAuthorId(createdPost.getId(), createdUser.getId());

        assertThat(exists).isTrue();
    }

    @Test
    void existsByIdAndAuthorId_shouldReturnFalse_whenPostIdOrAuthorIdDoNotExist() {
        boolean exists = postRepository.existsByIdAndAuthorId(9999L, 8888L);

        assertThat(exists).isFalse();
    }

    @Test
    void findById_shouldReturnPost_whenPostIdExists() {
        UserEntity user = UserFixtures.user();
        UserEntity createdUser = userRepository.save(user);
        PostEntity post = PostFixtures.post(createdUser);
        PostEntity createdPost = postRepository.save(post);

        Optional<PostEntity> foundPostOpt = postRepository.findById(createdPost.getId());

        assertThat(foundPostOpt).isPresent();
        PostEntity foundPost = foundPostOpt.get();
        assertThat(foundPost.getId()).isEqualTo(createdPost.getId());
        assertThat(foundPost.getTitle()).isEqualTo(post.getTitle());
        assertThat(foundPost.getSlug()).isEqualTo(post.getSlug());
        assertThat(foundPost.getPreviewImageUrl()).isEqualTo(post.getPreviewImageUrl());
        assertThat(foundPost.getDescription()).isEqualTo(post.getDescription());
        assertThat(foundPost.getContent()).isEqualTo(post.getContent());
        assertThat(foundPost.getAuthor().getId()).isEqualTo(createdUser.getId());
    }
}
