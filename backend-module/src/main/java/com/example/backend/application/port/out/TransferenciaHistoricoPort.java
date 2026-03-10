package com.example.backend.application.port.out;

import com.example.backend.application.shared.PageResult;
import com.example.backend.domain.model.TransferenciaHistorico;

public interface TransferenciaHistoricoPort {

    TransferenciaHistoricoPort NO_OP = new TransferenciaHistoricoPort() {
        @Override
        public TransferenciaHistorico save(TransferenciaHistorico historico) {
            return historico;
        }

        @Override
        public PageResult<TransferenciaHistorico> findAll(int page, int size) {
            return new PageResult<>(
                    java.util.List.of(),
                    0,
                    page,
                    size,
                    0,
                    false,
                    false
            );
        }
    };

    TransferenciaHistorico save(TransferenciaHistorico historico);

    PageResult<TransferenciaHistorico> findAll(int page, int size);
}
