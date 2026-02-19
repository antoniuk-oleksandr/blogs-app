package com.example.blogs.app.api.comment.repository;

import com.example.blogs.app.api.comment.entity.CommentEntity;
import com.example.blogs.app.api.file.entity.FileEntity;
import com.example.blogs.app.api.file.fixture.FileFixtures;
import com.example.blogs.app.api.file.repository.FileRepository;
import com.example.blogs.app.api.post.entity.PostEntity;
import com.example.blogs.app.api.post.fixture.PostFixtures;
import com.example.blogs.app.api.post.repository.PostRepository;
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
class CommentRepositoryTest extends AbstractPostgresTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private FileRepository fileRepository;

    @Test
    void findAllByPostId_shouldReturnAllPosts() {
        FileEntity fileToCreate = FileFixtures.file();
        FileEntity createdFile = fileRepository.save(fileToCreate);
        UserEntity userToCreate = UserFixtures.user(createdFile);
        UserEntity createdUser = userRepository.save(userToCreate);
        PostEntity post = PostFixtures.post(createdUser);
        PostEntity createdPost = postRepository.save(post);
        CommentEntity firstComment = CommentEntity.builder()
                .content("content")
                .post(createdPost)
                .author(createdUser)
                .build();
        CommentEntity secondComment = CommentEntity.builder()
                .content("content")
                .post(createdPost)
                .author(createdUser)
                .build();

        commentRepository.save(firstComment);
        commentRepository.save(secondComment);

        List<CommentEntity> comments = commentRepository.findAllByPostId(createdPost.getId());
        assertThat(comments)
                .isNotNull()
                .hasSize(2)
                .allMatch(CommentEntity.class::isInstance);
    }

    @Test
    void findAllByPostId_shouldReturnEmptyList_whenNoCommentsExist() {
        FileEntity fileToCreate = FileFixtures.file();
        FileEntity createdFile = fileRepository.save(fileToCreate);
        UserEntity userToCreate = UserFixtures.user(createdFile);
        UserEntity createdUser = userRepository.save(userToCreate);
        PostEntity post = PostFixtures.post(createdUser);
        PostEntity createdPost = postRepository.save(post);

        List<CommentEntity> comments = commentRepository.findAllByPostId(createdPost.getId());
        assertThat(comments)
                .isNotNull()
                .isEmpty();
    }

    @Test
    void save_shouldReturnSavedComment() {
        FileEntity fileToCreate = FileFixtures.file();
        FileEntity createdFile = fileRepository.save(fileToCreate);
        UserEntity userToCreate = UserFixtures.user(createdFile);
        UserEntity createdUser = userRepository.save(userToCreate);
        PostEntity post = PostFixtures.post(createdUser);
        PostEntity createdPost = postRepository.save(post);
        CommentEntity commentToCreate = CommentEntity.builder()
                .content("content")
                .post(createdPost)
                .author(createdUser)
                .build();

        CommentEntity createdComment = commentRepository.save(commentToCreate);

        assertThat(createdComment)
                .isNotNull();
        assertThat(createdComment.getId()).isNotNull();
        assertThat(createdComment.getContent()).isEqualTo(commentToCreate.getContent());
        assertThat(createdComment.getPost()).isEqualTo(commentToCreate.getPost());
        assertThat(createdComment.getAuthor()).isEqualTo(commentToCreate.getAuthor());
    }

    @Test
    void deleteById_shouldDeleteComment() {
        FileEntity fileToCreate = FileFixtures.file();
        FileEntity createdFile = fileRepository.save(fileToCreate);
        UserEntity userToCreate = UserFixtures.user(createdFile);
        UserEntity createdUser = userRepository.save(userToCreate);
        PostEntity post = PostFixtures.post(createdUser);
        PostEntity createdPost = postRepository.save(post);
        CommentEntity commentToCreate = CommentEntity.builder()
                .content("content")
                .post(createdPost)
                .author(createdUser)
                .build();

        CommentEntity createdComment = commentRepository.save(commentToCreate);

        commentRepository.deleteById(createdComment.getId());

        assertThat(commentRepository.findById(createdComment.getId())).isEmpty();
    }

    @Test
    void findById_shouldReturnComment() {
        FileEntity fileToCreate = FileFixtures.file();
        FileEntity createdFile = fileRepository.save(fileToCreate);
        UserEntity userToCreate = UserFixtures.user(createdFile);
        UserEntity createdUser = userRepository.save(userToCreate);
        PostEntity post = PostFixtures.post(createdUser);
        PostEntity createdPost = postRepository.save(post);
        CommentEntity commentToCreate = CommentEntity.builder()
                .content("content")
                .post(createdPost)
                .author(createdUser)
                .build();

        CommentEntity createdComment = commentRepository.save(commentToCreate);

        Optional<CommentEntity> result = commentRepository.findById(createdComment.getId());

        assertThat(result)
                .isPresent()
                .isEqualTo(Optional.of(createdComment));
    }
}
