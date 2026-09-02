package com.cobre.notification.infrastructure.config;

import org.junit.jupiter.api.Test;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;

import static org.junit.jupiter.api.Assertions.*;

class RabbitMQConfigTest {

    private final RabbitMQConfig config = new RabbitMQConfig();

    @Test
    void exchange_ShouldReturnCorrectDirectExchange() {
        DirectExchange exchange = config.exchange();
        assertNotNull(exchange);
        assertEquals(RabbitMQConfig.EXCHANGE, exchange.getName());
    }

    @Test
    void dlxExchange_ShouldReturnCorrectDirectExchange() {
        DirectExchange dlxExchange = config.dlxExchange();
        assertNotNull(dlxExchange);
        assertEquals(RabbitMQConfig.DLX_EXCHANGE, dlxExchange.getName());
    }

    @Test
    void notificationQueue_ShouldReturnCorrectQueue() {
        Queue queue = config.notificationQueue();
        assertNotNull(queue);
        assertEquals(RabbitMQConfig.QUEUE, queue.getName());
        assertEquals(RabbitMQConfig.DLX_EXCHANGE, queue.getArguments().get("x-dead-letter-exchange"));
        assertEquals(RabbitMQConfig.DLQ_ROUTING_KEY, queue.getArguments().get("x-dead-letter-routing-key"));
    }

    @Test
    void dlqQueue_ShouldReturnCorrectQueue() {
        Queue dlqQueue = config.dlqQueue();
        assertNotNull(dlqQueue);
        assertEquals(RabbitMQConfig.DLQ_QUEUE, dlqQueue.getName());
    }

    @Test
    void binding_ShouldReturnCorrectBinding() {
        Queue queue = config.notificationQueue();
        DirectExchange exchange = config.exchange();
        
        Binding binding = config.binding(queue, exchange);
        assertNotNull(binding);
        assertEquals(RabbitMQConfig.QUEUE, binding.getDestination());
        assertEquals(RabbitMQConfig.EXCHANGE, binding.getExchange());
        assertEquals(RabbitMQConfig.ROUTING_KEY, binding.getRoutingKey());
    }

    @Test
    void dlqBinding_ShouldReturnCorrectBinding() {
        Queue dlqQueue = config.dlqQueue();
        DirectExchange dlxExchange = config.dlxExchange();
        
        Binding binding = config.dlqBinding(dlqQueue, dlxExchange);
        assertNotNull(binding);
        assertEquals(RabbitMQConfig.DLQ_QUEUE, binding.getDestination());
        assertEquals(RabbitMQConfig.DLX_EXCHANGE, binding.getExchange());
        assertEquals(RabbitMQConfig.DLQ_ROUTING_KEY, binding.getRoutingKey());
    }
}
