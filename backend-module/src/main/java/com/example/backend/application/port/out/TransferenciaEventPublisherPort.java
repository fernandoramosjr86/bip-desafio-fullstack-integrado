package com.example.backend.application.port.out;

import com.example.backend.domain.model.TransferenciaHistorico;

public interface TransferenciaEventPublisherPort {

    TransferenciaEventPublisherPort NO_OP = historico -> {
    };

    void publish(TransferenciaHistorico historico);
}
