package com.cobre.notification.infrastructure.adapter.out.http;

import com.cobre.notification.domain.model.NotificationEvent;
import com.cobre.notification.domain.port.out.WebhookSenderPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class WebhookSenderAdapter implements WebhookSenderPort {

    private static final Logger log = LoggerFactory.getLogger(WebhookSenderAdapter.class);

    private final RestTemplate restTemplate;

    public WebhookSenderAdapter() {
        this.restTemplate = new RestTemplate();
    }

    @Override
    public boolean sendWebhook(NotificationEvent event, String signature) {
        if (event.getWebhookUrl() == null || event.getWebhookUrl().isBlank()) {
            log.warn("No webhook URL provided for eventId: {}. Simulating delivery attempt.", event.getEventId());
            return true;
        }

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("X-Cobre-Signature", signature);

            HttpEntity<String> requestEntity = new HttpEntity<>(event.getContent(), headers);
            ResponseEntity<String> response = restTemplate.postForEntity(event.getWebhookUrl(), requestEntity, String.class);

            return response.getStatusCode().is2xxSuccessful();
        } catch (Exception e) {
            log.error("Failed to deliver webhook to {} for eventId: {}. Error: {}", event.getWebhookUrl(), event.getEventId(), e.getMessage());
            return false;
        }
    }
}
