package com.example.blogs.app.api.post.service;

import com.github.slugify.Slugify;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * Generates unique URL-friendly slugs by combining slugified titles with random suffixes.
 */
@Service
@AllArgsConstructor
public class SlugServiceImpl implements SlugService {

    private final Slugify slugify;

    @Override
    public String generateSuffix() {
        return UUID.randomUUID().toString().substring(0, 12);
    }

    @Override
    public String generate(String title) {
        return slugify.slugify(title) + "-" + generateSuffix();
    }
}
