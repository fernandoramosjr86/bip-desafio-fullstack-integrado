package com.example.backend.adapters.out.messaging;

import com.example.backend.application.port.out.TransferenciaEventPublisherPort;
import com.example.backend.domain.model.TransferenciaHistorico;
import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(name = "app.transfer.jms.enabled", havingValue = "true")
@ConditionalOnBean(JmsTemplate.class)
public class TransferenciaEventJmsAdapter implements TransferenciaEventPublisherPort {

    private final JmsTemplate jmsTemplate;
    private final TransferenciaJmsProperties properties;

    public TransferenciaEventJmsAdapter(JmsTemplate jmsTemplate, TransferenciaJmsProperties properties) {
        this.jmsTemplate = jmsTemplate;
        this.properties = properties;
    }

    @Override
    public void publish(TransferenciaHistorico historico) {
        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("id", historico.id());
        payload.put("beneficioOrigemId", historico.beneficioOrigemId());
        payload.put("beneficioDestinoId", historico.beneficioDestinoId());
        payload.put("valor", historico.valor());
        payload.put("executadoEm", historico.executadoEm());
        jmsTemplate.convertAndSend(properties.queueName(), payload);
    }
}
