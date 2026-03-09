package com.example.backend.application.port.out;

import java.math.BigDecimal;

public interface BeneficioTransferenciaPort {
    void transferir(Long beneficioOrigemId, Long beneficioDestinoId, BigDecimal valor);
}
