package com.example.blogs.app.api.search.indexing;

import com.example.blogs.app.api.search.service.PostSearchIndexingService;
import lombok.AllArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@AllArgsConstructor
public class PostSearchIndexListener {
    private final PostSearchIndexingService indexingService;

    @RabbitListener(
            queues = "${search.indexing.queue}",
            containerFactory = "postSearchIndexListenerContainerFactory"
    )
    public void handle(List<PostSearchIndexEvent> events) {
        indexingService.indexBatch(events);
    }
}
