package com.cobre.notification.domain.service;

import com.cobre.notification.domain.model.DeliveryStatus;
import com.cobre.notification.domain.model.NotificationEvent;
import com.cobre.notification.domain.port.out.EventPublisherPort;
import com.cobre.notification.domain.port.out.NotificationRepositoryPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

class NotificationQueryServiceTest {

    @Mock
    private NotificationRepositoryPort repositoryPort;

    @Mock
    private EventPublisherPort eventPublisherPort;

    @InjectMocks
    private NotificationQueryService queryService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getEventById_ShouldReturnEvent_WhenFound() {
        NotificationEvent event = new NotificationEvent("EVT001", "payment", "content",
                LocalDateTime.now(), DeliveryStatus.COMPLETED, "CLIENT001", "https://webhook.site", 0);

        when(repositoryPort.findByIdAndClientId("EVT001", "CLIENT001")).thenReturn(Optional.of(event));

        NotificationEvent result = queryService.getEventById("EVT001", "CLIENT001");

        assertNotNull(result);
        assertEquals("EVT001", result.getEventId());
    }

    @Test
    void replayEvent_ShouldResetStatusToPendingAndPublish() {
        NotificationEvent event = new NotificationEvent("EVT001", "payment", "content",
                LocalDateTime.now(), DeliveryStatus.FAILED, "CLIENT001", "https://webhook.site", 3);

        when(repositoryPort.findByIdAndClientId("EVT001", "CLIENT001")).thenReturn(Optional.of(event));
        when(repositoryPort.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        NotificationEvent result = queryService.replayEvent("EVT001", "CLIENT001");

        assertEquals(DeliveryStatus.PENDING, result.getDeliveryStatus());
        assertEquals(0, result.getRetryCount());
        verify(eventPublisherPort, times(1)).publishDelivery(result);
    }
}
