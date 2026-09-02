package com.cobre.notification.domain.port.out;

import com.cobre.notification.domain.model.NotificationEvent;

public interface WebhookSenderPort {
    boolean sendWebhook(NotificationEvent event, String signature);
}
