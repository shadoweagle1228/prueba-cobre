package com.cobre.notification.infrastructure.adapter.in.messaging;

import com.cobre.notification.domain.model.DeliveryStatus;
import com.cobre.notification.domain.model.NotificationEvent;
import com.cobre.notification.domain.port.out.NotificationRepositoryPort;
import com.cobre.notification.domain.service.DeliveryService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RabbitMQConsumerAdapterTest {

    @Mock
    private DeliveryService deliveryService;

    @Mock
    private NotificationRepositoryPort repositoryPort;

    @InjectMocks
    private RabbitMQConsumerAdapter adapter;

    @Test
    void consumeNotificationMessage_WhenEventFound_CallsDeliveryService() {
        NotificationEvent event = new NotificationEvent("E1", "PAYMENT", "content", null, DeliveryStatus.PENDING, "", "url", 0);
        when(repositoryPort.findById("E1")).thenReturn(Optional.of(event));

        adapter.consumeNotificationMessage("E1");

        verify(deliveryService).processEventDelivery(event);
    }

    @Test
    void consumeNotificationMessage_WhenEventNotFound_DoesNotCallDeliveryService() {
        when(repositoryPort.findById("E1")).thenReturn(Optional.empty());

        adapter.consumeNotificationMessage("E1");

        verify(deliveryService, never()).processEventDelivery(any());
    }
}
