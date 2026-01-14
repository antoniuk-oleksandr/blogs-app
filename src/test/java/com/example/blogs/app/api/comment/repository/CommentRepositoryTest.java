package com.example.blogs.app.api.comment.repository;

import com.example.blogs.app.api.comment.entity.CommentEntity;
import com.example.blogs.app.api.post.entity.PostEntity;
import com.example.blogs.app.api.post.repository.PostRepository;
import com.example.blogs.app.api.user.entity.UserEntity;
import com.example.blogs.app.api.user.repository.UserRepository;
import com.example.blogs.app.support.AbstractPostgresTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;

import static org.assertj.core.api.Assertions.*;


@DataJpaTest
class CommentRepositoryTest extends AbstractPostgresTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Test
    void findAllByPostId_shouldReturnAllPosts() {
        UserEntity user = UserEntity.builder()
                .username("username")
                .passwordHash("passwordHash")
                .email("email")
                .build();
        UserEntity createdUser = userRepository.save(user);
        PostEntity post = PostEntity.builder()
                .title("title")
                .description("description")
                .content("content")
                .slug("slug")
                .previewImageUrl("previewImageUrl")
                .author(createdUser)
                .build();
        PostEntity createdPost = postRepository.save(post);
        CommentEntity comment = CommentEntity.builder()
                .content("content")
                .post(createdPost)
                .author(createdUser)
                .build();
        commentRepository.save(comment);
        comment = CommentEntity.builder()
                .content("content2")
                .post(createdPost)
                .author(createdUser)
                .build();
        commentRepository.save(comment);

        List<CommentEntity> comments = commentRepository.findAllByPostId(createdPost.getId());
        assertThat(comments)
                .isNotNull()
                .hasSize(2)
                .allMatch(CommentEntity.class::isInstance);
    }

    @Test
    void findAllByPostId_shouldReturnEmptyList_whenNoCommentsExist() {
        UserEntity user = UserEntity.builder()
                .username("username")
                .passwordHash("passwordHash")
                .email("email")
                .build();
        UserEntity createdUser = userRepository.save(user);
        PostEntity post = PostEntity.builder()
                .title("title")
                .description("description")
                .content("content")
                .slug("slug")
                .previewImageUrl("previewImageUrl")
                .author(createdUser)
                .build();
        PostEntity createdPost = postRepository.save(post);

        List<CommentEntity> comments = commentRepository.findAllByPostId(createdPost.getId());
        assertThat(comments)
                .isNotNull()
                .isEmpty();
    }
}
