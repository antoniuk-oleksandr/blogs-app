package com.example.blogs.app.config;

import com.github.slugify.Slugify;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SlugConfig {

    @Bean
    public Slugify slugify() {
        return Slugify.builder()
                .lowerCase(true)
                .build();
    }
}
