package com.cobre.notification.infrastructure.adapter.out.persistence;
import com.cobre.notification.domain.model.DeliveryStatus;
import com.cobre.notification.domain.model.NotificationEvent;
import com.cobre.notification.domain.port.out.NotificationRepositoryPort;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import java.time.LocalDateTime;
import java.util.Optional;
@Component
public class NotificationPersistenceAdapter implements NotificationRepositoryPort {
    private final SpringDataNotificationRepository springDataRepository;
    public NotificationPersistenceAdapter(SpringDataNotificationRepository springDataRepository) {
        this.springDataRepository = springDataRepository;
    }
    @Override
    public NotificationEvent save(NotificationEvent event) {
        NotificationEntity entity = toEntity(event);
        NotificationEntity saved = springDataRepository.save(entity);
        return toDomain(saved);
    }
    @Override
    public Optional<NotificationEvent> findByIdAndClientId(String eventId, String clientId) {
        return springDataRepository.findByEventIdAndClientId(eventId, clientId).map(this::toDomain);
    }
    @Override
    public Optional<NotificationEvent> findById(String eventId) {
        return springDataRepository.findById(eventId).map(this::toDomain);
    }
    @Override
    public Page<NotificationEvent> findByClientIdAndFilters(String clientId, LocalDateTime startDate, LocalDateTime endDate, DeliveryStatus status, Pageable pageable) {
        return springDataRepository.findByClientIdWithFilters(clientId, startDate, endDate, status, pageable).map(this::toDomain);
    }
    private NotificationEntity toEntity(NotificationEvent domain) {
        return new NotificationEntity(domain.getEventId(), domain.getEventType(), domain.getContent(), domain.getDeliveryDate(), domain.getDeliveryStatus(), domain.getClientId(), domain.getWebhookUrl(), domain.getRetryCount());
    }
    private NotificationEvent toDomain(NotificationEntity entity) {
        return new NotificationEvent(entity.getEventId(), entity.getEventType(), entity.getContent(), entity.getDeliveryDate(), entity.getDeliveryStatus(), entity.getClientId(), entity.getWebhookUrl(), entity.getRetryCount());
    }
}
