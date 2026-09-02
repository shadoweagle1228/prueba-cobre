package com.cobre.notification.infrastructure.adapter.in.rest;

import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void handleRuntimeException_WhenMessageContainsNotFound_ShouldReturn404() {
        RuntimeException ex = new RuntimeException("Notification event not found");
        ResponseEntity<Map<String, Object>> response = handler.handleRuntimeException(ex);
        
        assertEquals(404, response.getStatusCode().value());
        Map<String, Object> body = (Map<String, Object>) response.getBody();
        assertNotNull(body);
        assertEquals("Notification event not found", body.get("error"));
        assertEquals(404, body.get("status"));
        assertNotNull(body.get("timestamp"));
    }

    @Test
    void handleRuntimeException_WhenMessageContainsUnauthorized_ShouldReturn403() {
        RuntimeException ex = new RuntimeException("Unauthorized client");
        ResponseEntity<Map<String, Object>> response = handler.handleRuntimeException(ex);
        
        assertEquals(403, response.getStatusCode().value());
        Map<String, Object> body = (Map<String, Object>) response.getBody();
        assertNotNull(body);
        assertEquals("Unauthorized client", body.get("error"));
        assertEquals(403, body.get("status"));
        assertNotNull(body.get("timestamp"));
    }

    @Test
    void handleRuntimeException_WhenOtherException_ShouldReturn500() {
        RuntimeException ex = new RuntimeException("Some generic error");
        ResponseEntity<Map<String, Object>> response = handler.handleRuntimeException(ex);
        
        assertEquals(500, response.getStatusCode().value());
        Map<String, Object> body = (Map<String, Object>) response.getBody();
        assertNotNull(body);
        assertEquals("Some generic error", body.get("error"));
        assertEquals(500, body.get("status"));
        assertNotNull(body.get("timestamp"));
    }
}
