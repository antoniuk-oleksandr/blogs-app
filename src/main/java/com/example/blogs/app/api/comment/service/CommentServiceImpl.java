package com.example.blogs.app.api.comment.service;

import com.example.blogs.app.api.comment.dto.CommentCreateRequestDTO;
import com.example.blogs.app.api.comment.dto.CommentDTO;
import com.example.blogs.app.api.comment.entity.CommentEntity;
import com.example.blogs.app.api.comment.mapper.CommentMapper;
import com.example.blogs.app.api.comment.repository.adapter.CommentRepositoryAdapter;
import com.example.blogs.app.api.post.entity.PostEntity;
import com.example.blogs.app.api.user.entity.UserEntity;
import com.example.blogs.app.logging.MDCKeys;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Orchestrates comment retrieval operations by coordinating with the comment repository adapter.
 */
@Service
@AllArgsConstructor
public class CommentServiceImpl implements CommentService {

    private static final Logger log = LoggerFactory.getLogger(CommentServiceImpl.class);

    private final CommentRepositoryAdapter commentRepositoryAdapter;

    private final CommentMapper commentMapper;

    /**
     * Retrieves all comments associated with a specific post.
     * Delegates to the repository adapter for data retrieval.
     *
     * @param postId the ID of the post
     * @return list of comments for the post
     */
    @Override
    public List<CommentEntity> getCommentsByPostId(Long postId) {
        return commentRepositoryAdapter.findAllByPostId(postId);
    }

    /**
     * Creates a new comment on a specific post.
     * Constructs the comment entity, persists it via the repository adapter, and converts to DTO.
     *
     * @param postId     the ID of the post to comment on
     * @param userId     the ID of the user creating the comment
     * @param requestDTO request containing the comment content
     * @return newly created comment as DTO
     */
    @Override
    public CommentDTO createComment(Long postId, Long userId, CommentCreateRequestDTO requestDTO) {
        PostEntity post = PostEntity.builder()
                .id(postId)
                .build();
        UserEntity author = UserEntity.builder()
                .id(userId)
                .build();
        CommentEntity commentToSave = CommentEntity.builder()
                .post(post)
                .author(author)
                .content(requestDTO.content())
                .build();

        CommentEntity savedComment = commentRepositoryAdapter.save(commentToSave);

        log.info("comment_created commentId={} postId={} userId={} requestId={}",
                savedComment.getId(), postId, MDC.get(MDCKeys.USER_ID), MDC.get(MDCKeys.REQUEST_ID));

        return commentMapper.toCommentDTO(savedComment, postId, userId);
    }

    /**
     * Deletes a comment by its ID.
     * Delegates to the repository adapter for deletion and logs the operation.
     *
     * @param commentId the ID of the comment to delete
     */
    @Override
    public void deleteCommentById(Long commentId) {
        commentRepositoryAdapter.deleteById(commentId);
        log.info("comment_deleted commentId={} requestId={}", commentId, MDC.get(MDCKeys.REQUEST_ID));
    }
}
