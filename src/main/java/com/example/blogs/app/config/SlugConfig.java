package com.example.blogs.app.config;

import com.github.slugify.Slugify;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configures the Slugify library for generating URL-friendly slugs.
 */
@Configuration
public class SlugConfig {

    /**
     * Creates a Slugify instance configured to generate lowercase URL-friendly slugs.
     *
     * @return configured Slugify instance for slug generation
     */
    @Bean
    public Slugify slugify() {
        return Slugify.builder()
                .lowerCase(true)
                .build();
    }
}
