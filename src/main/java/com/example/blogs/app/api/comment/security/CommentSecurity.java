package com.example.blogs.app.api.comment.security;

import com.example.blogs.app.api.comment.exception.CommentNotFoundException;
import com.example.blogs.app.api.comment.repository.adapter.CommentRepositoryAdapter;
import com.example.blogs.app.security.UserPrincipal;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class CommentSecurity {

    private final CommentRepositoryAdapter commentRepositoryAdapter;

    private final Logger log = LoggerFactory.getLogger(CommentSecurity.class);

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
