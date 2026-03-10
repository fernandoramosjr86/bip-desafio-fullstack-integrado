package com.example.backend.adapters.out.messaging;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.transfer.jms")
public record TransferenciaJmsProperties(
        boolean enabled,
        String queueName
) {
    public TransferenciaJmsProperties {
        if (queueName == null || queueName.isBlank()) {
            queueName = "beneficios.transferencias";
        }
    }
}
