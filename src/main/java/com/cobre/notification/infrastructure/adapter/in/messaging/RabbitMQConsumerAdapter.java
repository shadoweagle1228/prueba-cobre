package com.cobre.notification.infrastructure.adapter.in.messaging;

import com.cobre.notification.domain.model.NotificationEvent;
import com.cobre.notification.domain.port.out.NotificationRepositoryPort;
import com.cobre.notification.domain.service.DeliveryService;
import com.cobre.notification.infrastructure.config.RabbitMQConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class RabbitMQConsumerAdapter {

    private static final Logger log = LoggerFactory.getLogger(RabbitMQConsumerAdapter.class);

    private final DeliveryService deliveryService;
    private final NotificationRepositoryPort repositoryPort;

    public RabbitMQConsumerAdapter(DeliveryService deliveryService, NotificationRepositoryPort repositoryPort) {
        this.deliveryService = deliveryService;
        this.repositoryPort = repositoryPort;
    }

    @RabbitListener(queues = RabbitMQConfig.QUEUE)
    public void consumeNotificationMessage(String eventId) {
        log.info("Consuming RabbitMQ message for eventId: {}", eventId);
        Optional<NotificationEvent> eventOpt = repositoryPort.findByIdAndClientId(eventId, getClientIdFromEvent(eventId));

        if (eventOpt.isPresent()) {
            deliveryService.processEventDelivery(eventOpt.get());
        } else {
            log.warn("EventId: {} not found in database for processing", eventId);
        }
    }

    private String getClientIdFromEvent(String eventId) {
        // Fallback helper if needed, query handled via repository
        return "";
    }
}
