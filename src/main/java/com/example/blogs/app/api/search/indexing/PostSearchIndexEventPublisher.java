package com.example.blogs.app.api.search.indexing;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

@Component
public class PostSearchIndexEventPublisher {

    private final RabbitTemplate rabbitTemplate;

    private final String exchange;

    private final String routingKey;

    public PostSearchIndexEventPublisher(
            RabbitTemplate rabbitTemplate,
            @Value("${search.indexing.exchange}") String exchange,
            @Value("${search.indexing.routing-key}") String routingKey
    ) {
        this.rabbitTemplate = rabbitTemplate;
        this.exchange = exchange;
        this.routingKey = routingKey;
    }

    public void publishUpsertAfterCommit(Long postId) {
        publishAfterCommit(new PostSearchIndexEvent(PostSearchIndexAction.UPSERT, postId));
    }

    public void publishDeleteAfterCommit(Long postId) {
        publishAfterCommit(new PostSearchIndexEvent(PostSearchIndexAction.DELETE, postId));
    }

    private void publishAfterCommit(PostSearchIndexEvent event) {
        if (!TransactionSynchronizationManager.isSynchronizationActive()) {
            publish(event);
            return;
        }

        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                publish(event);
            }
        });
    }

    private void publish(PostSearchIndexEvent event) {
        rabbitTemplate.convertAndSend(exchange, routingKey, event);
    }
}
