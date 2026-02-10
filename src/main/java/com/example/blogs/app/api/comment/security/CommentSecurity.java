package com.example.blogs.app.api.comment.security;

import com.example.blogs.app.api.comment.exception.CommentNotFoundException;
import com.example.blogs.app.api.comment.repository.adapter.CommentRepositoryAdapter;
import com.example.blogs.app.security.UserPrincipal;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

/**
 * Verifies comment ownership by coordinating with the comment repository and security context.
 */
@Component
@AllArgsConstructor
public class CommentSecurity {

    private final CommentRepositoryAdapter commentRepositoryAdapter;

    private final Logger log = LoggerFactory.getLogger(CommentSecurity.class);

    /**
     * Checks if the authenticated user is the owner of the specified comment.
     * Verifies comment existence before checking ownership.
     *
     * @param commentId the ID of the comment to check
     * @return true if the authenticated user owns the comment, false otherwise
     * @throws CommentNotFoundException if the comment does not exist
     */
    public boolean isOwner(Long commentId) {
        if (!commentRepositoryAdapter.existsById(commentId)) {
            log.warn("Comment not found: postId={}", commentId);
            throw new CommentNotFoundException(null);
        }

        UserPrincipal principal = (UserPrincipal) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();

        boolean isOwner = commentRepositoryAdapter.existsByIdAndAuthorId(commentId, principal.id());

        log.debug("Ownership check: commentId={} userId={} result={}", commentId, principal.id(), isOwner);

        return isOwner;
    }
}
