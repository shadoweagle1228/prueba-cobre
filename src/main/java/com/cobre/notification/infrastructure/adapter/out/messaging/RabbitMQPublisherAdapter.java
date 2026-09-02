package com.cobre.notification.infrastructure.adapter.out.messaging;

import com.cobre.notification.domain.model.NotificationEvent;
import com.cobre.notification.domain.port.out.EventPublisherPort;
import com.cobre.notification.infrastructure.config.RabbitMQConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
public class RabbitMQPublisherAdapter implements EventPublisherPort {

    private static final Logger log = LoggerFactory.getLogger(RabbitMQPublisherAdapter.class);

    private final RabbitTemplate rabbitTemplate;

    public RabbitMQPublisherAdapter(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    @Override
    public void publishDelivery(NotificationEvent event) {
        log.info("Publishing event to RabbitMQ queue for eventId: {}", event.getEventId());
        try {
            rabbitTemplate.convertAndSend(RabbitMQConfig.EXCHANGE, RabbitMQConfig.ROUTING_KEY, event.getEventId());
        } catch (Exception e) {
            log.error("Failed to publish to RabbitMQ: {}", e.getMessage());
        }
    }

    @Override
    public void sendToDlq(NotificationEvent event) {
        log.info("Publishing event to DLQ for eventId: {}", event.getEventId());
        try {
            rabbitTemplate.convertAndSend(RabbitMQConfig.DLX_EXCHANGE, RabbitMQConfig.DLQ_ROUTING_KEY, event.getEventId());
        } catch (Exception e) {
            log.error("Failed to publish to DLQ: {}", e.getMessage());
        }
    }
}
