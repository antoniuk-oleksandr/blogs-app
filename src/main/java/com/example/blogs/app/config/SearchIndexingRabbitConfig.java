package com.example.blogs.app.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SearchIndexingRabbitConfig {

    @Bean
    public Queue searchIndexingQueue(@Value("${search.indexing.queue}") String queue) {
        return new Queue(queue, true);
    }

    @Bean
    public DirectExchange searchIndexingExchange(@Value("${search.indexing.exchange}") String exchange) {
        return new DirectExchange(exchange, true, false);
    }

    @Bean
    public Binding searchIndexingBinding(
            Queue searchIndexingQueue,
            DirectExchange searchIndexingExchange,
            @Value("${search.indexing.routing-key}") String routingKey
    ) {
        return BindingBuilder.bind(searchIndexingQueue)
                .to(searchIndexingExchange)
                .with(routingKey);
    }

    @Bean
    public MessageConverter rabbitMessageConverter(ObjectMapper objectMapper) {
        return new Jackson2JsonMessageConverter(objectMapper);
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory, MessageConverter rabbitMessageConverter) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(rabbitMessageConverter);
        return rabbitTemplate;
    }

    @Bean
    public SimpleRabbitListenerContainerFactory postSearchIndexListenerContainerFactory(
            ConnectionFactory connectionFactory,
            MessageConverter rabbitMessageConverter,
            @Value("${search.indexing.batch-size}") int batchSize
    ) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(rabbitMessageConverter);
        factory.setBatchListener(true);
        factory.setConsumerBatchEnabled(true);
        factory.setBatchSize(batchSize);
        factory.setConcurrentConsumers(1);
        return factory;
    }
}
