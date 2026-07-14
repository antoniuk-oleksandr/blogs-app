package com.example.blogs.app.api.search.controller;

import com.example.blogs.app.api.search.dto.SearchPost;
import com.example.blogs.app.api.search.dto.SearchPostsResponseDTO;
import com.example.blogs.app.api.search.dto.SearchType;
import com.example.blogs.app.api.search.exception.FailedToSearchPostsException;
import com.example.blogs.app.api.search.fixture.SearchFixtures;
import com.example.blogs.app.api.search.service.SearchService;
import com.example.blogs.app.exception.ErrorResponseWriter;
import com.example.blogs.app.exception.ExceptionHttpStatusMapper;
import com.example.blogs.app.exception.GlobalExceptionHandler;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.hamcrest.Matchers.nullValue;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc(addFilters = false)
@WebMvcTest(controllers = SearchController.class)
@Import({
        GlobalExceptionHandler.class,
        ExceptionHttpStatusMapper.class,
        ErrorResponseWriter.class
})
class SearchControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private SearchService searchService;

    @Test
    @SneakyThrows
    void searchPosts_shouldReturn200_withDefaultRelevanceType() {
        List<SearchPost> posts = List.of(SearchFixtures.searchPost(1L));
        when(searchService.searchPosts("spring boot", SearchType.RELEVANCE, null))
                .thenReturn(new SearchPostsResponseDTO(posts, null, false));

        mockMvc.perform(get("/search").param("query", "spring boot"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.posts[0].id").value(1L))
                .andExpect(jsonPath("$.posts[0].title").value("title"))
                .andExpect(jsonPath("$.hasMore").value(false))
                .andExpect(jsonPath("$.nextCursor").value(nullValue()));

        verify(searchService).searchPosts("spring boot", SearchType.RELEVANCE, null);
    }

    @Test
    @SneakyThrows
    void searchPosts_shouldReturn200_whenTypeIsNewest() {
        List<SearchPost> posts = List.of(SearchFixtures.searchPost(1L), SearchFixtures.searchPost(2L));
        when(searchService.searchPosts("java", SearchType.NEWEST, null))
                .thenReturn(new SearchPostsResponseDTO(posts, "next", true));

        mockMvc.perform(get("/search").param("query", "java").param("type", "NEWEST"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.posts.length()").value(2))
                .andExpect(jsonPath("$.nextCursor").value("next"))
                .andExpect(jsonPath("$.hasMore").value(true));

        verify(searchService).searchPosts("java", SearchType.NEWEST, null);
    }

    @Test
    @SneakyThrows
    void searchPosts_shouldReturn200_whenTypeIsOldest() {
        List<SearchPost> posts = List.of(SearchFixtures.searchPost(1L));
        when(searchService.searchPosts("docker", SearchType.OLDEST, null))
                .thenReturn(new SearchPostsResponseDTO(posts, null, false));

        mockMvc.perform(get("/search").param("query", "docker").param("type", "OLDEST"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.posts[0].id").value(1L));

        verify(searchService).searchPosts("docker", SearchType.OLDEST, null);
    }

    @Test
    @SneakyThrows
    void searchPosts_shouldReturn200_withEmptyResults() {
        when(searchService.searchPosts(anyString(), any(), isNull()))
                .thenReturn(new SearchPostsResponseDTO(List.of(), null, false));

        mockMvc.perform(get("/search").param("query", "nothing"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.posts.length()").value(0));

        verify(searchService).searchPosts("nothing", SearchType.RELEVANCE, null);
    }

    @Test
    @SneakyThrows
    void searchPosts_shouldReturn500_whenServiceThrowsFaildToSearchPostsException() {
        when(searchService.searchPosts(anyString(), any(), isNull()))
                .thenThrow(new FailedToSearchPostsException(new RuntimeException("OpenSearch unreachable")));

        mockMvc.perform(get("/search").param("query", "java"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.message").value("Failed to search posts"));

        verify(searchService).searchPosts("java", SearchType.RELEVANCE, null);
    }

    @Test
    @SneakyThrows
    void searchPosts_shouldReturn500_whenServiceThrowsUnexpectedException() {
        when(searchService.searchPosts(anyString(), any(), isNull()))
                .thenThrow(new RuntimeException("Unexpected error"));

        mockMvc.perform(get("/search").param("query", "java"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.message").value("Unexpected error"));

        verify(searchService).searchPosts("java", SearchType.RELEVANCE, null);
    }
}
