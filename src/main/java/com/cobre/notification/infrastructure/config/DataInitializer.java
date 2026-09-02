package com.cobre.notification.infrastructure.config;

import com.cobre.notification.domain.model.DeliveryStatus;
import com.cobre.notification.infrastructure.adapter.out.persistence.NotificationEntity;
import com.cobre.notification.infrastructure.adapter.out.persistence.SpringDataNotificationRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
public class DataInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

    private final SpringDataNotificationRepository repository;
    private final ObjectMapper objectMapper;

    public DataInitializer(SpringDataNotificationRepository repository, ObjectMapper objectMapper) {
        this.repository = repository;
        this.objectMapper = objectMapper;
    }

    @Override
    public void run(String... args) throws Exception {
        if (repository.count() > 0) {
            log.info("Database already initialized with notification events.");
            return;
        }

        log.info("Loading initial notification events from notification_events.json...");
        InputStream inputStream = getClass().getResourceAsStream("/data/notification_events.json");
        if (inputStream == null) {
            inputStream = getClass().getClassLoader().getResourceAsStream("notification_events.json");
        }

        if (inputStream != null) {
            JsonNode root = objectMapper.readTree(inputStream);
            JsonNode eventsNode = root.get("events");
            DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;

            if (eventsNode != null && eventsNode.isArray()) {
                for (JsonNode node : eventsNode) {
                    String eventId = node.get("event_id").asText();
                    String eventType = node.get("event_type").asText();
                    String content = node.get("content").asText();
                    String dateStr = node.get("delivery_date").asText();
                    String statusStr = node.get("delivery_status").asText();
                    String clientId = node.get("client_id").asText();

                    LocalDateTime deliveryDate = LocalDateTime.parse(dateStr, formatter);
                    DeliveryStatus status = "completed".equalsIgnoreCase(statusStr) ? DeliveryStatus.COMPLETED : DeliveryStatus.FAILED;

                    NotificationEntity entity = new NotificationEntity(
                            eventId, eventType, content, deliveryDate, status, clientId, "https://webhook.site/test", 0
                    );
                    repository.save(entity);
                }
                log.info("Successfully loaded {} notification events into database.", repository.count());
            }
        } else {
            log.warn("notification_events.json not found in classpath resources.");
        }
    }
}
