package com.cobre.notification.domain.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;

class HmacSignerTest {

    private HmacSigner hmacSigner;

    @BeforeEach
    void setUp() {
        hmacSigner = new HmacSigner();
        ReflectionTestUtils.setField(hmacSigner, "hmacSecret", "test_secret_key");
    }

    @Test
    void calculateSignature_ShouldReturnValidHexSignature() {
        String payload = "{\"event_id\":\"EVT001\",\"content\":\"Test Payment\"}";
        String signature = hmacSigner.calculateSignature(payload);

        assertNotNull(signature);
        assertEquals(64, signature.length()); // SHA-256 hex length
    }

    @Test
    void calculateSignature_ShouldBeDeterministicForSamePayload() {
        String payload = "Test Content";
        String signature1 = hmacSigner.calculateSignature(payload);
        String signature2 = hmacSigner.calculateSignature(payload);

        assertEquals(signature1, signature2);
    }
}
