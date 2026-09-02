package com.cobre.notification.domain.service;

import com.cobre.notification.domain.model.DeliveryStatus;
import com.cobre.notification.domain.model.NotificationEvent;
import com.cobre.notification.domain.port.out.EventPublisherPort;
import com.cobre.notification.domain.port.out.NotificationRepositoryPort;
import com.cobre.notification.domain.port.out.WebhookSenderPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class DeliveryService {

    private static final Logger log = LoggerFactory.getLogger(DeliveryService.class);

    private final NotificationRepositoryPort repositoryPort;
    private final WebhookSenderPort webhookSenderPort;
    private final EventPublisherPort eventPublisherPort;
    private final HmacSigner hmacSigner;

    @Value("${app.webhook.max-retries:5}")
    private int maxRetries;

    public DeliveryService(NotificationRepositoryPort repositoryPort,
                           WebhookSenderPort webhookSenderPort,
                           EventPublisherPort eventPublisherPort,
                           HmacSigner hmacSigner) {
        this.repositoryPort = repositoryPort;
        this.webhookSenderPort = webhookSenderPort;
        this.eventPublisherPort = eventPublisherPort;
        this.hmacSigner = hmacSigner;
    }

    public void processEventDelivery(NotificationEvent event) {
        log.info("Processing webhook delivery for eventId: {}, clientId: {}", event.getEventId(), event.getClientId());

        String signature = hmacSigner.calculateSignature(event.getContent() != null ? event.getContent() : "");
        boolean success = webhookSenderPort.sendWebhook(event, signature);

        if (success) {
            event.setDeliveryStatus(DeliveryStatus.COMPLETED);
            event.setDeliveryDate(LocalDateTime.now());
            repositoryPort.save(event);
            log.info("Webhook successfully delivered for eventId: {}", event.getEventId());
        } else {
            int attempts = event.getRetryCount() + 1;
            event.setRetryCount(attempts);

            if (attempts >= maxRetries) {
                log.warn("Max retries ({}) reached for eventId: {}. Moving to DLQ.", maxRetries, event.getEventId());
                event.setDeliveryStatus(DeliveryStatus.DLQ);
                repositoryPort.save(event);
                eventPublisherPort.sendToDlq(event);
            } else {
                log.info("Delivery failed for eventId: {}. Scheduling retry #{}", event.getEventId(), attempts);
                event.setDeliveryStatus(DeliveryStatus.FAILED);
                repositoryPort.save(event);
                eventPublisherPort.publishDelivery(event);
            }
        }
    }
}
