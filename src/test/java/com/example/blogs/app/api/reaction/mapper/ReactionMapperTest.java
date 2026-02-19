package com.example.blogs.app.api.reaction.mapper;

import com.example.blogs.app.api.file.entity.FileEntity;
import com.example.blogs.app.api.file.fixture.FileFixtures;
import com.example.blogs.app.api.post.entity.PostEntity;
import com.example.blogs.app.api.post.fixture.PostFixtures;
import com.example.blogs.app.api.reaction.dto.ReactionDTO;
import com.example.blogs.app.api.reaction.entity.ReactionEntity;
import com.example.blogs.app.api.reaction.entity.ReactionType;
import com.example.blogs.app.api.reaction.fixture.ReactionFixtures;
import com.example.blogs.app.api.user.entity.UserEntity;
import com.example.blogs.app.api.user.fixture.UserFixtures;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.*;

class ReactionMapperTest {

    private final ReactionMapper reactionMapper = new ReactionMapperImpl();

    @Test
    void toReactionDTO_shouldMapEntityToDTO() {
        Long fileId = 1L;
        Long userId = 1L;
        Long postId = 1L;
        Long reactionId = 1L;
        LocalDateTime now = LocalDateTime.now();

        FileEntity file = FileFixtures.file(fileId, now);
        UserEntity user = UserFixtures.user(userId, file, now);
        PostEntity post = PostFixtures.post(postId, now, user);
        ReactionEntity reactionEntity = ReactionFixtures.reactionEntity(
                reactionId, now, user, post, ReactionType.LIKE
        );

        ReactionDTO reactionDTO = reactionMapper.toReactionDTO(reactionEntity);

        assertThat(reactionDTO).isNotNull();
        assertThat(reactionDTO.id()).isEqualTo(reactionId);
        assertThat(reactionDTO.reactionType()).isEqualTo(ReactionType.LIKE);
        assertThat(reactionDTO.userId()).isEqualTo(userId);
        assertThat(reactionDTO.postId()).isEqualTo(postId);
    }

    @Test
    void toReactionDTO_shouldReturnDTOwithNullUserIdAndPostId_whenUserAndPostAreNull() {
        Long reactionId = 1L;
        LocalDateTime now = LocalDateTime.now();

        ReactionEntity reactionEntity = ReactionFixtures.reactionEntity(
                reactionId, now, null, null, ReactionType.DISLIKE
        );

        ReactionDTO reactionDTO = reactionMapper.toReactionDTO(reactionEntity);

        assertThat(reactionDTO).isNotNull();
        assertThat(reactionDTO.id()).isEqualTo(reactionId);
        assertThat(reactionDTO.reactionType()).isEqualTo(ReactionType.DISLIKE);
        assertThat(reactionDTO.userId()).isNull();
        assertThat(reactionDTO.postId()).isNull();
    }

    @Test
    void toReactionDTO_shouldReturnNull_whenEntityIsNull() {
        ReactionEntity reactionEntity = null;

        ReactionDTO reactionDTO = reactionMapper.toReactionDTO(reactionEntity);

        assertThat(reactionDTO).isNull();
    }
}
