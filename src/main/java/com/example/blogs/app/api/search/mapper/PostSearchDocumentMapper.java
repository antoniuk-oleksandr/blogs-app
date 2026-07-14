package com.example.blogs.app.api.search.mapper;

import com.example.blogs.app.api.file.entity.FileEntity;
import com.example.blogs.app.api.file.service.FileUrlBuilder;
import com.example.blogs.app.api.post.entity.PostEntity;
import com.example.blogs.app.api.reaction.entity.ReactionType;
import com.example.blogs.app.api.comment.repository.adapter.CommentRepositoryAdapter;
import com.example.blogs.app.api.reaction.repository.adapter.ReactionRepositoryAdapter;
import com.example.blogs.app.api.search.dto.SearchPost;
import com.example.blogs.app.api.search.dto.SearchPostAuthor;
import com.example.blogs.app.api.user.entity.UserEntity;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@AllArgsConstructor
public class PostSearchDocumentMapper {

    private final CommentRepositoryAdapter commentRepositoryAdapter;

    private final ReactionRepositoryAdapter reactionRepositoryAdapter;

    private final FileUrlBuilder fileUrlBuilder;

    public SearchPost toSearchPost(PostEntity post) {
        long postId = post.getId();
        return new SearchPost(
                postId,
                post.getTitle(),
                post.getDescription(),
                post.getContent(),
                post.getSlug(),
                post.getCreatedAt(),
                Math.toIntExact(reactionRepositoryAdapter.countByPostIdAndReactionType(postId, ReactionType.LIKE)),
                Math.toIntExact(commentRepositoryAdapter.countByPostId(postId)),
                Optional.ofNullable(post.getFile()).map(fileUrlBuilder::build).orElse(null),
                toSearchPostAuthor(post.getAuthor())
        );
    }

    private SearchPostAuthor toSearchPostAuthor(UserEntity author) {
        String profilePictureUrl = Optional.ofNullable(author.getFile())
                .map(fileUrlBuilder::build)
                .orElse(null);
        return new SearchPostAuthor(
                author.getId(),
                author.getUsername(),
                null,
                null,
                profilePictureUrl
        );
    }
}
