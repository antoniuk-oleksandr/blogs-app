package com.example.blogs.app.api.search.service;

import com.example.blogs.app.api.search.dto.CursorData;
import com.example.blogs.app.api.search.dto.OpenSearchPostHit;
import com.example.blogs.app.api.search.dto.SearchPost;
import com.example.blogs.app.api.search.dto.SearchPostsResponseDTO;
import com.example.blogs.app.api.search.dto.SearchType;
import com.example.blogs.app.api.search.exception.FailedToDecodeCursorException;
import com.example.blogs.app.api.search.exception.FailedToEncodeCursorException;
import com.example.blogs.app.api.search.exception.FailedToSearchPostsException;
import com.example.blogs.app.api.search.exception.InvalidCursorException;
import com.example.blogs.app.api.search.fixture.SearchFixtures;
import com.example.blogs.app.util.CursorUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.opensearch.client.opensearch._types.FieldValue;
import org.opensearch.client.opensearch.core.SearchRequest;

import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OpenSearchServiceTest {

    @Mock
    private OpenSearchQueryExecutor queryExecutor;

    @Mock
    private OpenSearchQueryBuilder queryBuilder;

    @Mock
    private CursorUtils cursorUtils;

    private OpenSearchService openSearchService;

    @BeforeEach
    void setUp() {
        openSearchService = new OpenSearchService(queryExecutor, queryBuilder, cursorUtils);
    }

    @Test
    void searchPosts_shouldDelegateToExecutorWithRelevanceQuery_whenTypeIsRelevance() {
        String query = "spring boot";
        List<OpenSearchPostHit> expected = List.of(searchHit(1L));
        SearchRequest request = mock(SearchRequest.class);
        when(queryBuilder.buildRelevanceQuery(query, null, 21)).thenReturn(request);
        when(queryExecutor.execute(request)).thenReturn(expected);

        SearchPostsResponseDTO result = openSearchService.searchPosts(query, SearchType.RELEVANCE, null);

        assertThat(result.posts()).containsExactly(expected.getFirst().post());
        assertThat(result.hasMore()).isFalse();
        assertThat(result.nextCursor()).isNull();
        verify(queryBuilder).buildRelevanceQuery(query, null, 21);
        verify(queryExecutor).execute(request);
        verifyNoMoreInteractions(queryBuilder, queryExecutor);
    }

    @Test
    void searchPosts_shouldDelegateToExecutorWithNewestQuery_whenTypeIsNewest() {
        String query = "java";
        List<OpenSearchPostHit> expected = List.of(searchHit(1L), searchHit(2L));
        SearchRequest request = mock(SearchRequest.class);
        when(queryBuilder.buildNewestQuery(query, null, 21)).thenReturn(request);
        when(queryExecutor.execute(request)).thenReturn(expected);

        SearchPostsResponseDTO result = openSearchService.searchPosts(query, SearchType.NEWEST, null);

        assertThat(result.posts()).containsExactly(expected.get(0).post(), expected.get(1).post());
        verify(queryBuilder).buildNewestQuery(query, null, 21);
        verify(queryExecutor).execute(request);
        verifyNoMoreInteractions(queryBuilder, queryExecutor);
    }

    @Test
    void searchPosts_shouldDelegateToExecutorWithOldestQuery_whenTypeIsOldest() {
        String query = "docker";
        List<OpenSearchPostHit> expected = List.of(searchHit(1L));
        SearchRequest request = mock(SearchRequest.class);
        when(queryBuilder.buildOldestQuery(query, null, 21)).thenReturn(request);
        when(queryExecutor.execute(request)).thenReturn(expected);

        SearchPostsResponseDTO result = openSearchService.searchPosts(query, SearchType.OLDEST, null);

        assertThat(result.posts()).containsExactly(expected.getFirst().post());
        verify(queryBuilder).buildOldestQuery(query, null, 21);
        verify(queryExecutor).execute(request);
        verifyNoMoreInteractions(queryBuilder, queryExecutor);
    }

    @Test
    void searchPosts_shouldReturnEmptyList_whenExecutorReturnsEmpty() {
        String query = "nonexistent";
        SearchRequest request = mock(SearchRequest.class);
        when(queryBuilder.buildRelevanceQuery(query, null, 21)).thenReturn(request);
        when(queryExecutor.execute(request)).thenReturn(List.of());

        SearchPostsResponseDTO result = openSearchService.searchPosts(query, SearchType.RELEVANCE, null);

        assertThat(result.posts()).isEmpty();
        assertThat(result.hasMore()).isFalse();
        assertThat(result.nextCursor()).isNull();
        verify(queryBuilder).buildRelevanceQuery(query, null, 21);
        verify(queryExecutor).execute(request);
    }

    @Test
    void searchPosts_shouldDecodeCursorAndPassCursorDataToQueryBuilder() {
        String query = "java";
        String cursor = "cursor";
        CursorData cursorData = new CursorData(List.of(1.0, 2L));
        SearchRequest request = mock(SearchRequest.class);
        List<OpenSearchPostHit> expected = List.of(searchHit(1L));
        when(cursorUtils.decode(cursor)).thenReturn(cursorData);
        when(queryBuilder.buildNewestQuery(query, cursorData, 21)).thenReturn(request);
        when(queryExecutor.execute(request)).thenReturn(expected);

        SearchPostsResponseDTO result = openSearchService.searchPosts(query, SearchType.NEWEST, cursor);

        assertThat(result.posts()).containsExactly(expected.getFirst().post());
        verify(cursorUtils).decode(cursor);
        verify(queryBuilder).buildNewestQuery(query, cursorData, 21);
        verify(queryExecutor).execute(request);
    }

    @Test
    void searchPosts_shouldReturnNextCursor_whenMoreResultsExist() {
        String query = "spring";
        SearchRequest request = mock(SearchRequest.class);
        List<OpenSearchPostHit> hits = java.util.stream.LongStream.rangeClosed(1, 21)
                .mapToObj(this::searchHit)
                .toList();
        when(queryBuilder.buildRelevanceQuery(query, null, 21)).thenReturn(request);
        when(queryExecutor.execute(request)).thenReturn(hits);
        when(cursorUtils.encode(new CursorData(List.of(1.0, 20L)))).thenReturn("next-cursor");

        SearchPostsResponseDTO result = openSearchService.searchPosts(query, SearchType.RELEVANCE, null);

        assertThat(result.posts()).hasSize(20);
        assertThat(result.hasMore()).isTrue();
        assertThat(result.nextCursor()).isEqualTo("next-cursor");
        assertThat(result.posts()).doesNotContain(hits.getLast().post());
        verify(cursorUtils).encode(new CursorData(List.of(1.0, 20L)));
    }

    @Test
    void searchPosts_shouldEncodeStringAndBooleanSortValuesInNextCursor() {
        String query = "spring";
        SearchRequest request = mock(SearchRequest.class);
        List<OpenSearchPostHit> hits = java.util.stream.LongStream.rangeClosed(1, 19)
                .mapToObj(this::searchHit)
                .collect(java.util.stream.Collectors.toCollection(java.util.ArrayList::new));
        hits.add(searchHit(20L, FieldValue.of("slug"), FieldValue.of(true)));
        hits.add(searchHit(21L));
        when(queryBuilder.buildRelevanceQuery(query, null, 21)).thenReturn(request);
        when(queryExecutor.execute(request)).thenReturn(hits);
        when(cursorUtils.encode(new CursorData(List.of("slug", true)))).thenReturn("next-cursor");

        SearchPostsResponseDTO result = openSearchService.searchPosts(query, SearchType.RELEVANCE, null);

        assertThat(result.nextCursor()).isEqualTo("next-cursor");
        verify(cursorUtils).encode(new CursorData(List.of("slug", true)));
    }

    @Test
    void searchPosts_shouldThrowInvalidCursorException_whenCursorDecodeFails() {
        when(cursorUtils.decode("bad-cursor"))
                .thenThrow(new FailedToDecodeCursorException(new RuntimeException("decode failed")));

        assertThatThrownBy(() -> openSearchService.searchPosts("query", SearchType.RELEVANCE, "bad-cursor"))
                .isInstanceOf(InvalidCursorException.class);
    }

    @Test
    void searchPosts_shouldThrowInvalidCursorException_whenCursorEncodeFails() {
        String query = "spring";
        SearchRequest request = mock(SearchRequest.class);
        List<OpenSearchPostHit> hits = java.util.stream.LongStream.rangeClosed(1, 21)
                .mapToObj(this::searchHit)
                .toList();
        when(queryBuilder.buildRelevanceQuery(query, null, 21)).thenReturn(request);
        when(queryExecutor.execute(request)).thenReturn(hits);
        when(cursorUtils.encode(new CursorData(List.of(1.0, 20L))))
                .thenThrow(new FailedToEncodeCursorException(new RuntimeException("encode failed")));

        assertThatThrownBy(() -> openSearchService.searchPosts(query, SearchType.RELEVANCE, null))
                .isInstanceOf(InvalidCursorException.class);
    }

    @Test
    void searchPosts_shouldThrowFailedToSearchPostsException_whenExecutorFails() {
        String query = "spring";
        SearchRequest request = mock(SearchRequest.class);
        when(queryBuilder.buildRelevanceQuery(query, null, 21)).thenReturn(request);
        when(queryExecutor.execute(request)).thenThrow(new RuntimeException("OpenSearch failed"));

        assertThatThrownBy(() -> openSearchService.searchPosts(query, SearchType.RELEVANCE, null))
                .isInstanceOf(FailedToSearchPostsException.class);
    }

    private OpenSearchPostHit searchHit(Long id) {
        SearchPost post = SearchFixtures.searchPost(id);
        return new OpenSearchPostHit(post, 1.0, List.of(FieldValue.of(1.0), FieldValue.of(id)));
    }

    private OpenSearchPostHit searchHit(Long id, FieldValue firstSortValue, FieldValue secondSortValue) {
        SearchPost post = SearchFixtures.searchPost(id);
        return new OpenSearchPostHit(post, 1.0, List.of(firstSortValue, secondSortValue));
    }
}
