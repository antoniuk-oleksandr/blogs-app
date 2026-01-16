package com.example.blogs.app.api.post.security;

import com.example.blogs.app.api.post.exception.PostNotFoundException;
import com.example.blogs.app.api.post.repository.adapter.PostRepositoryAdapter;
import com.example.blogs.app.security.UserPrincipal;
import lombok.AllArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component("postSecurity")
@AllArgsConstructor
public class PostSecurityImpl implements PostSecurity {

    private final PostRepositoryAdapter postRepositoryAdapter;

    public boolean isOwner(Long postId) {
        if (!postRepositoryAdapter.existsById(postId)) {
            throw new PostNotFoundException(null);
        }

        UserPrincipal principal = (UserPrincipal) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();

        return postRepositoryAdapter
                .existsByIdAndAuthorId(postId, principal.id());
    }
}
