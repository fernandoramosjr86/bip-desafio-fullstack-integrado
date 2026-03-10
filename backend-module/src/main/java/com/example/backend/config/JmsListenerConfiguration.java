package com.example.backend.config;

import jakarta.jms.ConnectionFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.annotation.EnableJms;

@Configuration
@EnableJms
@ConditionalOnProperty(name = "app.transfer.jms.enabled", havingValue = "true")
@ConditionalOnBean(ConnectionFactory.class)
public class JmsListenerConfiguration {
}
