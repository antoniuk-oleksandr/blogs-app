package com.example.blogs.app.api.search.indexing;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class PostSearchIndexEventPublisherTest {

    private static final String EXCHANGE = "blogs.search.indexing";

    private static final String ROUTING_KEY = "posts.index";

    @Mock
    private RabbitTemplate rabbitTemplate;

    private PostSearchIndexEventPublisher publisher;

    @BeforeEach
    void setUp() {
        publisher = new PostSearchIndexEventPublisher(rabbitTemplate, EXCHANGE, ROUTING_KEY);
    }

    @AfterEach
    void tearDown() {
        if (TransactionSynchronizationManager.isSynchronizationActive()) {
            TransactionSynchronizationManager.clearSynchronization();
        }
    }

    @Test
    void publishUpsertAfterCommit_shouldPublishImmediately_whenTransactionSynchronizationIsNotActive() {
        publisher.publishUpsertAfterCommit(1L);

        ArgumentCaptor<PostSearchIndexEvent> eventCaptor = ArgumentCaptor.forClass(PostSearchIndexEvent.class);
        verify(rabbitTemplate).convertAndSend(eq(EXCHANGE), eq(ROUTING_KEY), eventCaptor.capture());
        assertThat(eventCaptor.getValue()).isEqualTo(new PostSearchIndexEvent(PostSearchIndexAction.UPSERT, 1L));
    }

    @Test
    void publishDeleteAfterCommit_shouldPublishImmediately_whenTransactionSynchronizationIsNotActive() {
        publisher.publishDeleteAfterCommit(1L);

        ArgumentCaptor<PostSearchIndexEvent> eventCaptor = ArgumentCaptor.forClass(PostSearchIndexEvent.class);
        verify(rabbitTemplate).convertAndSend(eq(EXCHANGE), eq(ROUTING_KEY), eventCaptor.capture());
        assertThat(eventCaptor.getValue()).isEqualTo(new PostSearchIndexEvent(PostSearchIndexAction.DELETE, 1L));
    }

    @Test
    void publishUpsertAfterCommit_shouldPublishOnlyAfterCommit_whenTransactionSynchronizationIsActive() {
        TransactionSynchronizationManager.initSynchronization();

        publisher.publishUpsertAfterCommit(1L);

        verify(rabbitTemplate, never()).convertAndSend(EXCHANGE, ROUTING_KEY, new PostSearchIndexEvent(PostSearchIndexAction.UPSERT, 1L));

        TransactionSynchronizationManager.getSynchronizations()
                .forEach(TransactionSynchronization::afterCommit);

        ArgumentCaptor<PostSearchIndexEvent> eventCaptor = ArgumentCaptor.forClass(PostSearchIndexEvent.class);
        verify(rabbitTemplate).convertAndSend(eq(EXCHANGE), eq(ROUTING_KEY), eventCaptor.capture());
        assertThat(eventCaptor.getValue()).isEqualTo(new PostSearchIndexEvent(PostSearchIndexAction.UPSERT, 1L));
    }
}
