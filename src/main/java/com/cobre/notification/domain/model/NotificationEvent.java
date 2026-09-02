package com.cobre.notification.domain.model;

import java.time.LocalDateTime;

public class NotificationEvent {

    private String eventId;
    private String eventType;
    private String content;
    private LocalDateTime deliveryDate;
    private DeliveryStatus deliveryStatus;
    private String clientId;
    private String webhookUrl;
    private int retryCount;

    public NotificationEvent() {
    }

    public NotificationEvent(String eventId, String eventType, String content,
                             LocalDateTime deliveryDate, DeliveryStatus deliveryStatus,
                             String clientId, String webhookUrl, int retryCount) {
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
