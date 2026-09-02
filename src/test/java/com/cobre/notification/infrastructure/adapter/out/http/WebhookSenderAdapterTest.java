package com.cobre.notification.infrastructure.adapter.out.http;

import com.cobre.notification.domain.model.DeliveryStatus;
import com.cobre.notification.domain.model.NotificationEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WebhookSenderAdapterTest {

    private WebhookSenderAdapter adapter;

    @Mock
    private RestTemplate restTemplate;

    @BeforeEach
    void setUp() {
        adapter = new WebhookSenderAdapter();
        ReflectionTestUtils.setField(adapter, "restTemplate", restTemplate);
    }

    @Test
    void sendWebhook_WithBlankUrl_ShouldReturnTrue() {
        NotificationEvent event = new NotificationEvent("E1", "PAYMENT", "content", null, DeliveryStatus.PENDING, "C1", "", 0);
        
        boolean result = adapter.sendWebhook(event, "sig");
        
        assertTrue(result);
        verifyNoInteractions(restTemplate);
    }

    @Test
    void sendWebhook_WhenRestTemplateSucceeds_ShouldReturnTrue() {
        NotificationEvent event = new NotificationEvent("E1", "PAYMENT", "content", null, DeliveryStatus.PENDING, "C1", "http://example.com", 0);
        ResponseEntity<String> response = new ResponseEntity<>("OK", HttpStatus.OK);
        
        when(restTemplate.postForEntity(eq("http://example.com"), any(HttpEntity.class), eq(String.class)))
                .thenReturn(response);
                
        boolean result = adapter.sendWebhook(event, "sig");
        
        assertTrue(result);
    }

    @Test
    void sendWebhook_WhenRestTemplateThrowsException_ShouldReturnFalse() {
        NotificationEvent event = new NotificationEvent("E1", "PAYMENT", "content", null, DeliveryStatus.PENDING, "C1", "http://example.com", 0);
        
        when(restTemplate.postForEntity(eq("http://example.com"), any(HttpEntity.class), eq(String.class)))
                .thenThrow(new RestClientException("Connection refused"));
                
        boolean result = adapter.sendWebhook(event, "sig");
        
        assertFalse(result);
    }
}
