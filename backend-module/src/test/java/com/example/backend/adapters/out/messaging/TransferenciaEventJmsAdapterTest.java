package com.example.backend.adapters.out.messaging;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.verify;

import com.example.backend.domain.model.TransferenciaHistorico;
import java.math.BigDecimal;
import java.time.Instant;
import org.junit.jupiter.api.Test;
import org.springframework.jms.core.JmsTemplate;

class TransferenciaEventJmsAdapterTest {

    @Test
    void publishDeveEnviarPayloadParaFilaConfigurada() {
        JmsTemplate jmsTemplate = org.mockito.Mockito.mock(JmsTemplate.class);
        TransferenciaJmsProperties properties = new TransferenciaJmsProperties(true, "fila.transferencias");
        TransferenciaEventJmsAdapter adapter = new TransferenciaEventJmsAdapter(jmsTemplate, properties);

        TransferenciaHistorico historico = new TransferenciaHistorico(
                10L,
                1L,
                2L,
                new BigDecimal("3.50"),
                Instant.parse("2026-03-09T12:00:00Z")
        );

        adapter.publish(historico);

        verify(jmsTemplate).convertAndSend(eq("fila.transferencias"), isA(java.util.Map.class));
    }
}
