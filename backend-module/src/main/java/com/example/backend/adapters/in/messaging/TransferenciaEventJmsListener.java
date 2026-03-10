package com.example.backend.adapters.in.messaging;

import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(name = "app.transfer.jms.enabled", havingValue = "true")
public class TransferenciaEventJmsListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(TransferenciaEventJmsListener.class);
    private final String queueName;

    public TransferenciaEventJmsListener(
            @Value("${app.transfer.jms.queue-name:beneficios.transferencias}") String queueName
    ) {
        this.queueName = queueName;
    }

    @JmsListener(destination = "${app.transfer.jms.queue-name:beneficios.transferencias}")
    public void onMessage(Map<String, Object> payload) {
        LOGGER.info("Evento JMS consumido da fila {}: {}", queueName, payload);
    }
}
