package com.cobre.notification.domain.port.out;

import com.cobre.notification.domain.model.NotificationEvent;

public interface EventPublisherPort {
    void publishDelivery(NotificationEvent event);
    void sendToDlq(NotificationEvent event);
}
