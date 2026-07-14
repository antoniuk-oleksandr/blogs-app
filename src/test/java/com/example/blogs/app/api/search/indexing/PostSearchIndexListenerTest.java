package com.example.blogs.app.api.search.indexing;

import com.example.blogs.app.api.search.service.PostSearchIndexingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class PostSearchIndexListenerTest {

    @Mock
    private PostSearchIndexingService indexingService;

    private PostSearchIndexListener listener;

    @BeforeEach
    void setUp() {
        listener = new PostSearchIndexListener(indexingService);
    }

    @Test
    void handle_shouldDelegateEventsToIndexingService() {
        List<PostSearchIndexEvent> events = List.of(
                new PostSearchIndexEvent(PostSearchIndexAction.UPSERT, 1L),
                new PostSearchIndexEvent(PostSearchIndexAction.DELETE, 2L)
        );

        listener.handle(events);

        verify(indexingService).indexBatch(events);
    }
}
