package com.example.blogs.app.api.search.mapper;

import com.example.blogs.app.api.comment.repository.adapter.CommentRepositoryAdapter;
import com.example.blogs.app.api.file.entity.FileEntity;
import com.example.blogs.app.api.file.fixture.FileFixtures;
import com.example.blogs.app.api.file.service.FileUrlBuilder;
import com.example.blogs.app.api.post.entity.PostEntity;
import com.example.blogs.app.api.post.fixture.PostFixtures;
import com.example.blogs.app.api.reaction.entity.ReactionType;
import com.example.blogs.app.api.reaction.repository.adapter.ReactionRepositoryAdapter;
import com.example.blogs.app.api.search.dto.SearchPost;
import com.example.blogs.app.api.user.entity.UserEntity;
import com.example.blogs.app.api.user.fixture.UserFixtures;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PostSearchDocumentMapperTest {

    @Mock
    private CommentRepositoryAdapter commentRepositoryAdapter;

    @Mock
    private ReactionRepositoryAdapter reactionRepositoryAdapter;

    @Mock
    private FileUrlBuilder fileUrlBuilder;

    private PostSearchDocumentMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new PostSearchDocumentMapper(commentRepositoryAdapter, reactionRepositoryAdapter, fileUrlBuilder);
    }

    @Test
    void toSearchPost_shouldMapPostToSearchDocument() {
        LocalDateTime now = LocalDateTime.now().withNano(0);
        FileEntity file = FileFixtures.file(1L, now);
        UserEntity author = UserFixtures.user(1L, file, now);
        PostEntity post = PostFixtures.post(10L, now, author, file);
        when(reactionRepositoryAdapter.countByPostIdAndReactionType(post.getId(), ReactionType.LIKE)).thenReturn(7L);
        when(commentRepositoryAdapter.countByPostId(post.getId())).thenReturn(3L);
        when(fileUrlBuilder.build(file)).thenReturn("fileUrl");

        SearchPost result = mapper.toSearchPost(post);

        assertThat(result.id()).isEqualTo(post.getId());
        assertThat(result.title()).isEqualTo(post.getTitle());
        assertThat(result.description()).isEqualTo(post.getDescription());
        assertThat(result.slug()).isEqualTo(post.getSlug());
        assertThat(result.content()).isEqualTo(post.getContent());
        assertThat(result.likesCount()).isEqualTo(7);
        assertThat(result.commentsCount()).isEqualTo(3);
        assertThat(result.previewPictureUrl()).isEqualTo("fileUrl");
        assertThat(result.author().id()).isEqualTo(author.getId());
        assertThat(result.author().username()).isEqualTo(author.getUsername());
        assertThat(result.author().profilePictureUrl()).isEqualTo("fileUrl");
    }
}
