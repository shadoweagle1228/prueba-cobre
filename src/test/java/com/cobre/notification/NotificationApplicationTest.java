package com.cobre.notification;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

import org.springframework.amqp.rabbit.core.RabbitTemplate;

@SpringBootTest
@ActiveProfiles("test")
class NotificationApplicationTest {

    @MockBean
    private RabbitTemplate rabbitTemplate;

    @MockBean(name = "dataInitializer")
    private Object dataInitializer;

    @Test
    void contextLoads() {
        // Test that application context loads successfully
    }
}
