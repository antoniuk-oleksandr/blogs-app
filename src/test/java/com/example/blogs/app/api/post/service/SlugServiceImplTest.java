package com.example.blogs.app.api.post.service;

import com.github.slugify.Slugify;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SlugServiceImplTest {

    @Mock
    private Slugify slugify;

    private SlugService slugService;

    @BeforeEach
    void setUp() {
        slugService = new SlugServiceImpl(slugify);
    }

    @Test
    void generateSuffix_shouldReturn12CharacterSuffix() {
        String suffix = slugService.generateSuffix();

        assertThat(suffix).hasSize(12);
    }

    @Test
    void generateSuffix_shouldReturnUniqueSuffixes() {
        String suffix1 = slugService.generateSuffix();
        String suffix2 = slugService.generateSuffix();

        assertThat(suffix1).isNotEqualTo(suffix2);
    }

    @Test
    void generate_shouldReturnSlugWithSuffix() {
        String title = "Test Title";
        String slugifiedTitle = "test-title";

        when(slugify.slugify(title)).thenReturn(slugifiedTitle);

        String result = slugService.generate(title);

        assertThat(result)
                .startsWith(slugifiedTitle + "-")
                .hasSize(slugifiedTitle.length() + 12 + 1);
    }
}
