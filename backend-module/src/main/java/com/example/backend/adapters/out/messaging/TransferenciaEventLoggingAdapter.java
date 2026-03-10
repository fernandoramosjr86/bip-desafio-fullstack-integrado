package com.example.backend.adapters.out.messaging;

import com.example.backend.application.port.out.TransferenciaEventPublisherPort;
import com.example.backend.domain.model.TransferenciaHistorico;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(name = "app.transfer.jms.enabled", havingValue = "false", matchIfMissing = true)
public class TransferenciaEventLoggingAdapter implements TransferenciaEventPublisherPort {

    private static final Logger LOGGER = LoggerFactory.getLogger(TransferenciaEventLoggingAdapter.class);

    @Override
    public void publish(TransferenciaHistorico historico) {
        LOGGER.info(
                "Evento de transferencia (modo log) origem={} destino={} valor={} executadoEm={}",
                historico.beneficioOrigemId(),
                historico.beneficioDestinoId(),
                historico.valor(),
                historico.executadoEm()
        );
    }
}
