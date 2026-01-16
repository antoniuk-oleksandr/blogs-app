package com.example.blogs.app.api.post.security;

public interface PostSecurity {

    public boolean isOwner(Long postId);
}
