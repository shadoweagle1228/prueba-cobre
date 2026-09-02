package com.cobre.notification.domain.service;

import com.cobre.notification.domain.model.DeliveryStatus;
import com.cobre.notification.domain.model.NotificationEvent;
import com.cobre.notification.domain.port.out.EventPublisherPort;
import com.cobre.notification.domain.port.out.NotificationRepositoryPort;
import com.cobre.notification.domain.port.out.WebhookSenderPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class DeliveryServiceTest {

    @Mock
    private NotificationRepositoryPort repositoryPort;

    @Mock
    private WebhookSenderPort webhookSenderPort;

    @Mock
    private EventPublisherPort eventPublisherPort;

    @Mock
    private HmacSigner hmacSigner;

    @InjectMocks
    private DeliveryService deliveryService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        ReflectionTestUtils.setField(deliveryService, "maxRetries", 5);
        when(hmacSigner.calculateSignature(any())).thenReturn("dummy_signature_hex");
    }

    @Test
    void processEventDelivery_SuccessfulWebhook_ShouldSetCompleted() {
        NotificationEvent event = new NotificationEvent("EVT001", "payment", "content",
                LocalDateTime.now(), DeliveryStatus.PENDING, "CLIENT001", "https://webhook.site", 0);

        when(webhookSenderPort.sendWebhook(eq(event), any())).thenReturn(true);

        deliveryService.processEventDelivery(event);

        assertEquals(DeliveryStatus.COMPLETED, event.getDeliveryStatus());
        verify(repositoryPort, times(1)).save(event);
    }

    @Test
    void processEventDelivery_FailedWebhook_ShouldIncrementRetryAndRepublish() {
        NotificationEvent event = new NotificationEvent("EVT001", "payment", "content",
                LocalDateTime.now(), DeliveryStatus.PENDING, "CLIENT001", "https://webhook.site", 0);

        when(webhookSenderPort.sendWebhook(eq(event), any())).thenReturn(false);

        deliveryService.processEventDelivery(event);

        assertEquals(1, event.getRetryCount());
        assertEquals(DeliveryStatus.FAILED, event.getDeliveryStatus());
        verify(eventPublisherPort, times(1)).publishDelivery(event);
    }

    @Test
    void processEventDelivery_MaxRetriesReached_ShouldRouteToDlq() {
        NotificationEvent event = new NotificationEvent("EVT001", "payment", "content",
                LocalDateTime.now(), DeliveryStatus.FAILED, "CLIENT001", "https://webhook.site", 4);

        when(webhookSenderPort.sendWebhook(eq(event), any())).thenReturn(false);

        deliveryService.processEventDelivery(event);

        assertEquals(5, event.getRetryCount());
        assertEquals(DeliveryStatus.DLQ, event.getDeliveryStatus());
        verify(eventPublisherPort, times(1)).sendToDlq(event);
    }
}
