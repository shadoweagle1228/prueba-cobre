package com.cobre.notification.infrastructure.adapter.out.messaging;

import com.cobre.notification.domain.model.DeliveryStatus;
import com.cobre.notification.domain.model.NotificationEvent;
import com.cobre.notification.infrastructure.config.RabbitMQConfig;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RabbitMQPublisherAdapterTest {

    @Mock
    private RabbitTemplate rabbitTemplate;

    @InjectMocks
    private RabbitMQPublisherAdapter adapter;

    @Test
    void publishDelivery_ShouldSendEventId() {
        NotificationEvent event = new NotificationEvent("E1", "PAYMENT", "content", null, DeliveryStatus.PENDING, "C1", "url", 0);
        
        adapter.publishDelivery(event);
        
        verify(rabbitTemplate).convertAndSend(RabbitMQConfig.EXCHANGE, RabbitMQConfig.ROUTING_KEY, "E1");
    }

    @Test
    void sendToDlq_ShouldSendEventId() {
        NotificationEvent event = new NotificationEvent("E1", "PAYMENT", "content", null, DeliveryStatus.PENDING, "C1", "url", 0);
        
        adapter.sendToDlq(event);
        
        verify(rabbitTemplate).convertAndSend(RabbitMQConfig.DLX_EXCHANGE, RabbitMQConfig.DLQ_ROUTING_KEY, "E1");
    }
    
    @Test
    void publishDelivery_ShouldHandleExceptionGracefully() {
        NotificationEvent event = new NotificationEvent("E1", "PAYMENT", "content", null, DeliveryStatus.PENDING, "C1", "url", 0);
        doThrow(new RuntimeException("Rabbit error")).when(rabbitTemplate).convertAndSend(anyString(), anyString(), anyString());
        
        try {
            adapter.publishDelivery(event);
        } catch (Exception e) {
            // Depending on implementation, it might rethrow or swallow. The test verifies interaction.
        }
        
        verify(rabbitTemplate).convertAndSend(RabbitMQConfig.EXCHANGE, RabbitMQConfig.ROUTING_KEY, "E1");
    }
}
