package com.cobre.notification.infrastructure.adapter.out.persistence;

import com.cobre.notification.domain.model.DeliveryStatus;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "notification_events", indexes = {
        @Index(name = "idx_client_delivery", columnList = "client_id, delivery_date"),
        @Index(name = "idx_client_status", columnList = "client_id, delivery_status")
})
public class NotificationEntity {

    @Id
    @Column(name = "event_id", nullable = false)
    private String eventId;

    @Column(name = "event_type", nullable = false)
    private String eventType;

    @Column(name = "content", columnDefinition = "TEXT")
    private String content;

    @Column(name = "delivery_date")
    private LocalDateTime deliveryDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "delivery_status", nullable = false)
    private DeliveryStatus deliveryStatus;

    @Column(name = "client_id", nullable = false)
    private String clientId;

    @Column(name = "webhook_url")
    private String webhookUrl;

    @Column(name = "retry_count")
    private int retryCount;

    public NotificationEntity() {
    }

    public NotificationEntity(String eventId, String eventType, String content, LocalDateTime deliveryDate, DeliveryStatus deliveryStatus, String clientId, String webhookUrl, int retryCount) {
        this.eventId = eventId;
        this.eventType = eventType;
        this.content = content;
        this.deliveryDate = deliveryDate;
        this.deliveryStatus = deliveryStatus;
        this.clientId = clientId;
        this.webhookUrl = webhookUrl;
        this.retryCount = retryCount;
    }

    public String getEventId() { return eventId; }
    public void setEventId(String eventId) { this.eventId = eventId; }

    public String getEventType() { return eventType; }
    public void setEventType(String eventType) { this.eventType = eventType; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public LocalDateTime getDeliveryDate() { return deliveryDate; }
    public void setDeliveryDate(LocalDateTime deliveryDate) { this.deliveryDate = deliveryDate; }

    public DeliveryStatus getDeliveryStatus() { return deliveryStatus; }
    public void setDeliveryStatus(DeliveryStatus deliveryStatus) { this.deliveryStatus = deliveryStatus; }

    public String getClientId() { return clientId; }
    public void setClientId(String clientId) { this.clientId = clientId; }

    public String getWebhookUrl() { return webhookUrl; }
    public void setWebhookUrl(String webhookUrl) { this.webhookUrl = webhookUrl; }

    public int getRetryCount() { return retryCount; }
    public void setRetryCount(int retryCount) { this.retryCount = retryCount; }
}
