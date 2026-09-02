package com.cobre.notification.infrastructure.adapter.in.rest;

import com.cobre.notification.domain.model.DeliveryStatus;
import com.cobre.notification.domain.model.NotificationEvent;
import com.cobre.notification.domain.service.NotificationQueryService;
import com.cobre.notification.infrastructure.config.JwtAuthFilter;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = NotificationController.class)
@AutoConfigureMockMvc(addFilters = false)
class NotificationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private NotificationQueryService queryService;

    @MockBean
    private JwtAuthFilter jwtAuthFilter;

    @Test
    @WithMockUser(username = "CLIENT001")
    void getNotificationEvents_ShouldReturnPageOfEvents() throws Exception {
        NotificationEvent event = new NotificationEvent("EVT001", "payment", "content",
                LocalDateTime.now(), DeliveryStatus.COMPLETED, "CLIENT001", "https://webhook.site", 0);

        when(queryService.getEventsForClient(eq("CLIENT001"), any(), any(), any(), any()))
                .thenReturn(new PageImpl<>(List.of(event)));

        mockMvc.perform(get("/notification_events")
                        .header("X-Client-Id", "CLIENT001")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].eventId").value("EVT001"))
                .andExpect(jsonPath("$.content[0].clientId").value("CLIENT001"));
    }

    @Test
    @WithMockUser(username = "CLIENT001")
    void getNotificationEventById_ShouldReturnEvent() throws Exception {
        NotificationEvent event = new NotificationEvent("EVT001", "payment", "content",
                LocalDateTime.now(), DeliveryStatus.COMPLETED, "CLIENT001", "https://webhook.site", 0);

        when(queryService.getEventById("EVT001", "CLIENT001")).thenReturn(event);

        mockMvc.perform(get("/notification_events/EVT001")
                        .header("X-Client-Id", "CLIENT001")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.eventId").value("EVT001"));
    }

    @Test
    @WithMockUser(username = "CLIENT002")
    void replayNotificationEvent_ShouldTriggerReplay() throws Exception {
        NotificationEvent event = new NotificationEvent("EVT003", "transfer", "content",
                LocalDateTime.now(), DeliveryStatus.PENDING, "CLIENT002", "https://webhook.site", 0);

        when(queryService.replayEvent("EVT003", "CLIENT002")).thenReturn(event);

        mockMvc.perform(post("/notification_events/EVT003/replay")
                        .header("X-Client-Id", "CLIENT002")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.eventId").value("EVT003"))
                .andExpect(jsonPath("$.deliveryStatus").value("PENDING"));
    }
}
