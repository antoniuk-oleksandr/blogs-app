package com.example.blogs.app.api.search.service;

import com.example.blogs.app.api.post.entity.PostEntity;
import com.example.blogs.app.api.post.fixture.PostFixtures;
import com.example.blogs.app.api.post.repository.adapter.PostRepositoryAdapter;
import com.example.blogs.app.api.search.dto.SearchPost;
import com.example.blogs.app.api.search.fixture.SearchFixtures;
import com.example.blogs.app.api.search.indexing.PostSearchIndexAction;
import com.example.blogs.app.api.search.indexing.PostSearchIndexEvent;
import com.example.blogs.app.api.search.mapper.PostSearchDocumentMapper;
import com.example.blogs.app.api.user.entity.UserEntity;
import com.example.blogs.app.api.user.fixture.UserFixtures;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.opensearch.client.opensearch.OpenSearchClient;
import org.opensearch.client.opensearch.core.BulkRequest;
import org.opensearch.client.opensearch.core.BulkResponse;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PostSearchIndexingServiceTest {

    @Mock
    private PostRepositoryAdapter postRepositoryAdapter;

    @Mock
    private PostSearchDocumentMapper documentMapper;

    @Mock
    private OpenSearchClient openSearchClient;

    private PostSearchIndexingService indexingService;

    @BeforeEach
    void setUp() {
        indexingService = new PostSearchIndexingService(postRepositoryAdapter, documentMapper, openSearchClient);
    }

    @Test
    void indexBatch_shouldBuildBulkRequestForUpsertAndDeleteEvents() throws IOException {
        LocalDateTime now = LocalDateTime.now().withNano(0);
        UserEntity author = UserFixtures.user(null);
        PostEntity post = PostFixtures.post(1L, now, author);
        SearchPost searchPost = SearchFixtures.searchPost(1L);
        when(postRepositoryAdapter.findAllByIdIn(List.of(1L))).thenReturn(List.of(post));
        when(documentMapper.toSearchPost(post)).thenReturn(searchPost);
        when(openSearchClient.bulk(any(BulkRequest.class))).thenReturn(successResponse());

        indexingService.indexBatch(List.of(
                new PostSearchIndexEvent(PostSearchIndexAction.UPSERT, 1L),
                new PostSearchIndexEvent(PostSearchIndexAction.DELETE, 2L)
        ));

        ArgumentCaptor<BulkRequest> requestCaptor = ArgumentCaptor.forClass(BulkRequest.class);
        verify(openSearchClient).bulk(requestCaptor.capture());
        BulkRequest request = requestCaptor.getValue();
        assertThat(request.index()).isEqualTo("posts");
        assertThat(request.operations()).hasSize(2);
        assertThat(request.operations().get(0).isDelete()).isTrue();
        assertThat(request.operations().get(0).delete().id()).isEqualTo("2");
        assertThat(request.operations().get(1).isIndex()).isTrue();
        assertThat(request.operations().get(1).index().id()).isEqualTo("1");
    }

    @Test
    void indexBatch_shouldNotCallOpenSearch_whenEventsAreEmpty() {
        indexingService.indexBatch(List.of());

        verifyNoInteractions(openSearchClient, postRepositoryAdapter, documentMapper);
    }

    @Test
    void indexBatch_shouldThrowIllegalStateException_whenBulkReturnsErrors() throws IOException {
        when(openSearchClient.bulk(any(BulkRequest.class))).thenReturn(BulkResponse.of(b -> b
                .errors(true)
                .items(List.of())
                .took(1)
        ));

        List<PostSearchIndexEvent> events = List.of(new PostSearchIndexEvent(PostSearchIndexAction.DELETE, 1L));

        assertThatThrownBy(() -> indexingService.indexBatch(events))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("OpenSearch bulk indexing completed with item errors");
    }

    private BulkResponse successResponse() {
        return BulkResponse.of(b -> b
                .errors(false)
                .items(List.of())
                .took(1)
        );
    }
}
