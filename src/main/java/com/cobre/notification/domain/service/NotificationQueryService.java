package com.cobre.notification.domain.service;

import com.cobre.notification.domain.model.DeliveryStatus;
import com.cobre.notification.domain.model.NotificationEvent;
import com.cobre.notification.domain.port.out.EventPublisherPort;
import com.cobre.notification.domain.port.out.NotificationRepositoryPort;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class NotificationQueryService {

    private final NotificationRepositoryPort repositoryPort;
    private final EventPublisherPort eventPublisherPort;

    public NotificationQueryService(NotificationRepositoryPort repositoryPort, EventPublisherPort eventPublisherPort) {
        this.repositoryPort = repositoryPort;
        this.eventPublisherPort = eventPublisherPort;
    }

    public Page<NotificationEvent> getEventsForClient(String clientId, LocalDateTime startDate, LocalDateTime endDate, DeliveryStatus status, Pageable pageable) {
        return repositoryPort.findByClientIdAndFilters(clientId, startDate, endDate, status, pageable);
    }

    public NotificationEvent getEventById(String eventId, String clientId) {
        return repositoryPort.findByIdAndClientId(eventId, clientId)
                .orElseThrow(() -> new RuntimeException("Notification event not found: " + eventId));
    }

    public NotificationEvent replayEvent(String eventId, String clientId) {
        NotificationEvent event = getEventById(eventId, clientId);
        event.setDeliveryStatus(DeliveryStatus.PENDING);
        event.setRetryCount(0);
        NotificationEvent updated = repositoryPort.save(event);
        eventPublisherPort.publishDelivery(updated);
        return updated;
    }
}
