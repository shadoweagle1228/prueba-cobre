package com.cobre.notification.infrastructure.adapter.in.rest;

import com.cobre.notification.domain.model.DeliveryStatus;
import com.cobre.notification.domain.model.NotificationEvent;
import com.cobre.notification.domain.service.NotificationQueryService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/notification_events")
public class NotificationController {

    private final NotificationQueryService queryService;

    public NotificationController(NotificationQueryService queryService) {
        this.queryService = queryService;
    }

    private String getAuthenticatedClientId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getPrincipal() == null) {
            throw new RuntimeException("Unauthorized client");
        }
        return auth.getPrincipal().toString();
    }

    @GetMapping
    public ResponseEntity<Page<NotificationEvent>> getNotificationEvents(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            @RequestParam(required = false) DeliveryStatus deliveryStatus,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        String clientId = getAuthenticatedClientId();
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "deliveryDate"));
        Page<NotificationEvent> events = queryService.getEventsForClient(clientId, startDate, endDate, deliveryStatus, pageRequest);
        return ResponseEntity.ok(events);
    }

    @GetMapping("/{notification_event_id}")
    public ResponseEntity<NotificationEvent> getNotificationEventById(
            @PathVariable("notification_event_id") String eventId) {

        String clientId = getAuthenticatedClientId();
        NotificationEvent event = queryService.getEventById(eventId, clientId);
        return ResponseEntity.ok(event);
    }

    @PostMapping("/{notification_event_id}/replay")
    public ResponseEntity<NotificationEvent> replayNotificationEvent(
            @PathVariable("notification_event_id") String eventId) {

        String clientId = getAuthenticatedClientId();
        NotificationEvent event = queryService.replayEvent(eventId, clientId);
        return ResponseEntity.ok(event);
    }
}
