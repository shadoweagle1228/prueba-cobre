package com.cobre.notification.domain.port.out;

import com.cobre.notification.domain.model.DeliveryStatus;
import com.cobre.notification.domain.model.NotificationEvent;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.Optional;

public interface NotificationRepositoryPort {
    NotificationEvent save(NotificationEvent event);
    Optional<NotificationEvent> findByIdAndClientId(String eventId, String clientId);
    Page<NotificationEvent> findByClientIdAndFilters(String clientId, LocalDateTime startDate, LocalDateTime endDate, DeliveryStatus status, Pageable pageable);
}
