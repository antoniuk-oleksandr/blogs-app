package com.example.blogs.app.api.post.service;

/**
 * Service for generating unique URL-friendly slugs from post titles.
 */
public interface SlugService {

    /**
     * Generates a random unique suffix for slug uniqueness.
     *
     * @return 12-character random suffix
     */
    String generateSuffix();

    /**
     * Generates a unique slug from a title with appended suffix.
     *
     * @param title the title to convert to slug
     * @return URL-friendly slug with unique suffix
     */
    String generate(String title);
}

