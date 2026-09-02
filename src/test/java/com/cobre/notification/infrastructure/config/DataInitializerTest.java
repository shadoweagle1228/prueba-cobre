package com.cobre.notification.infrastructure.config;

import com.cobre.notification.infrastructure.adapter.out.persistence.NotificationEntity;
import com.cobre.notification.infrastructure.adapter.out.persistence.SpringDataNotificationRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DataInitializerTest {

    @Mock
    private SpringDataNotificationRepository repository;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private DataInitializer dataInitializer;

    @Test
    void run_WhenRepositoryIsNotEmpty_ShouldNotLoadData() throws Exception {
        when(repository.count()).thenReturn(5L);

        dataInitializer.run();

        verify(repository, never()).save(any());
        verify(objectMapper, never()).readTree(any(java.io.InputStream.class));
    }

    @Test
    void run_WhenRepositoryIsEmpty_ShouldLoadData() throws Exception {
        when(repository.count()).thenReturn(0L);
        
        // Use a real ObjectMapper for this test to parse the actual JSON in resources
        DataInitializer realInit = new DataInitializer(repository, new ObjectMapper());
        
        realInit.run();
        
        // It should parse notification_events.json and save entities (assuming 10 records in json)
        verify(repository, atLeastOnce()).save(any(NotificationEntity.class));
    }
}
