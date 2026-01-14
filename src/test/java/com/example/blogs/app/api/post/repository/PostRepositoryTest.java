package com.example.blogs.app.api.post.repository;

import com.example.blogs.app.api.post.entity.PostEntity;
import com.example.blogs.app.api.user.entity.UserEntity;
import com.example.blogs.app.api.user.repository.UserRepository;
import com.example.blogs.app.support.AbstractPostgresTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;

import static org.assertj.core.api.Assertions.*;

@DataJpaTest
class PostRepositoryTest extends AbstractPostgresTest {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    void findByAuthorId_shouldReturnPosts_whenAuthorIdExists() {
        UserEntity user = UserEntity.builder()
                .username("testuser")
                .passwordHash("hashedpassword")
                .email("test")
                .build();
        PostEntity post = PostEntity.builder()
                .title("Sample Title")
                .slug("sample-title")
                .description("Sample Description")
                .previewImageUrl("preview.jpg")
                .content("Sample Content")
                .author(user)
                .build();
        UserEntity createdUser = userRepository.save(user);
        postRepository.save(post);

        List<PostEntity> foundPosts = postRepository.findByAuthorId(createdUser.getId());

        assertThat(foundPosts).isNotEmpty();
        assertThat(foundPosts.getFirst().getId()).isPositive();
        assertThat(foundPosts.getFirst().getTitle()).isEqualTo("Sample Title");
        assertThat(foundPosts.getFirst().getSlug()).isEqualTo("sample-title");
        assertThat(foundPosts.getFirst().getPreviewImageUrl()).isEqualTo("preview.jpg");
        assertThat(foundPosts.getFirst().getDescription()).isEqualTo("Sample Description");
        assertThat(foundPosts.getFirst().getContent()).isEqualTo("Sample Content");
        assertThat(foundPosts.getFirst().getAuthor().getId()).isEqualTo(createdUser.getId());
    }

    @Test
    void deleteByIdReturningCount_shouldReturnCount_whenPostIdExists() {
        UserEntity user = UserEntity.builder()
                .username("testuser")
                .passwordHash("hashedpassword")
                .email("test")
                .build();
        UserEntity createdUser = userRepository.save(user);
        PostEntity post = PostEntity.builder()
                .title("Sample Title")
                .slug("sample-title")
                .description("Sample Description")
                .previewImageUrl("preview.jpg")
                .content("Sample Content")
                .author(createdUser)
                .build();
        PostEntity createdPost = postRepository.save(post);

        Long deletedCount = postRepository.deleteByIdReturningCount(createdPost.getId());

        assertThat(deletedCount).isEqualTo(createdPost.getId());
    }

    @Test
    void deleteByIdReturningCount_shouldReturnNull_whenPostIdDoesNotExist() {
        Long deletedCount = postRepository.deleteByIdReturningCount(9999L);

        assertThat(deletedCount).isNull();
    }
}
