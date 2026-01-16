package com.example.blogs.app.api.post.security;

/**
 * Verifies post ownership for authorization purposes.
 */
public interface PostSecurity {

    /**
     * Checks if the authenticated user is the owner of the specified post.
     *
     * @param postId the ID of the post to check
     * @return true if the authenticated user owns the post, false otherwise
     */
    public boolean isOwner(Long postId);
}
