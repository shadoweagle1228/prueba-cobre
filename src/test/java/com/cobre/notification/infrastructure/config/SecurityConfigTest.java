package com.cobre.notification.infrastructure.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.test.context.ActiveProfiles;

import org.springframework.amqp.rabbit.core.RabbitTemplate;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@ActiveProfiles("test")
class SecurityConfigTest {

    @Autowired
    private SecurityFilterChain securityFilterChain;
    
    @MockBean
    private JwtAuthFilter jwtAuthFilter;

    @MockBean
    private RabbitTemplate rabbitTemplate;

    @MockBean(name = "dataInitializer")
    private Object dataInitializer; // Mock data initializer to prevent file loading

    @Test
    void securityFilterChain_ShouldBeCreated() {
        assertNotNull(securityFilterChain, "SecurityFilterChain should be configured and present in context");
    }
}
