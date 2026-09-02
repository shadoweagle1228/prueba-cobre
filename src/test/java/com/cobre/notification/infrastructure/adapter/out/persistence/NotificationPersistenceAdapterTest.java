package com.cobre.notification.infrastructure.adapter.out.persistence;

import com.cobre.notification.domain.model.DeliveryStatus;
import com.cobre.notification.domain.model.NotificationEvent;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificationPersistenceAdapterTest {

    @Mock
    private SpringDataNotificationRepository springDataRepository;

    @InjectMocks
    private NotificationPersistenceAdapter adapter;

    @Test
    void save_ShouldReturnSavedNotificationEvent() {
        NotificationEvent event = new NotificationEvent("E1", "PAYMENT", "content", null, DeliveryStatus.PENDING, "C1", "url", 0);
        NotificationEntity entity = new NotificationEntity("E1", "PAYMENT", "content", null, DeliveryStatus.PENDING, "C1", "url", 0);

        when(springDataRepository.save(any(NotificationEntity.class))).thenReturn(entity);

        NotificationEvent result = adapter.save(event);

        assertNotNull(result);
        assertEquals("E1", result.getEventId());
        verify(springDataRepository).save(any(NotificationEntity.class));
    }

    @Test
    void findByIdAndClientId_ShouldReturnEventWhenFound() {
        NotificationEntity entity = new NotificationEntity("E1", "PAYMENT", "content", null, DeliveryStatus.PENDING, "C1", "url", 0);
        when(springDataRepository.findByEventIdAndClientId("E1", "C1")).thenReturn(Optional.of(entity));

        Optional<NotificationEvent> result = adapter.findByIdAndClientId("E1", "C1");

        assertTrue(result.isPresent());
        assertEquals("E1", result.get().getEventId());
    }

    @Test
    void findByClientIdAndFilters_ShouldReturnPageOfEvents() {
        NotificationEntity entity = new NotificationEntity("E1", "PAYMENT", "content", null, DeliveryStatus.PENDING, "C1", "url", 0);
        Page<NotificationEntity> page = new PageImpl<>(List.of(entity));
        Pageable pageable = PageRequest.of(0, 10);
        LocalDateTime now = LocalDateTime.now();

        when(springDataRepository.findByClientIdWithFilters("C1", now, now, DeliveryStatus.PENDING, pageable))
                .thenReturn(page);

        Page<NotificationEvent> result = adapter.findByClientIdAndFilters("C1", now, now, DeliveryStatus.PENDING, pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals("E1", result.getContent().get(0).getEventId());
    }
}
