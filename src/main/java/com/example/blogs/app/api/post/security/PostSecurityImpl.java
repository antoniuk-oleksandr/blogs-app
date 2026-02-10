package com.example.blogs.app.api.post.security;

import com.example.blogs.app.api.post.exception.PostNotFoundException;
import com.example.blogs.app.api.post.repository.adapter.PostRepositoryAdapter;
import com.example.blogs.app.security.UserPrincipal;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

/**
 * Verifies post ownership by coordinating with the post repository and security context.
 */
@Component("postSecurity")
@AllArgsConstructor
public class PostSecurityImpl implements PostSecurity {

    private final PostRepositoryAdapter postRepositoryAdapter;

    private final Logger log = LoggerFactory.getLogger(PostSecurityImpl.class);

    /**
     * Checks if the authenticated user is the owner of the specified post.
     * Verifies post existence before checking ownership.
     *
     * @param postId the ID of the post to check
     * @return true if the authenticated user owns the post, false otherwise
     * @throws PostNotFoundException if the post does not exist
     */
    public boolean isOwner(Long postId) {
        if (!postRepositoryAdapter.existsById(postId)) {
            log.warn("Post not found: postId={}", postId);
            throw new PostNotFoundException(null);
        }

        UserPrincipal principal = (UserPrincipal) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();

        boolean isOwner = postRepositoryAdapter.existsByIdAndAuthorId(postId, principal.id());

        log.debug("Ownership check: postId={} userId={} result={}", postId, principal.id(), isOwner);

        return isOwner;
    }
}
