package com.example.backend.config;

import com.example.backend.adapters.out.messaging.TransferenciaJmsProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(TransferenciaJmsProperties.class)
public class MessagingConfiguration {
}
