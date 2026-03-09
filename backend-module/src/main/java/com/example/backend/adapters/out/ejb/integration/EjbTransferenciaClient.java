package com.example.backend.adapters.out.ejb.integration;

import java.math.BigDecimal;

public interface EjbTransferenciaClient {

    void transferir(Long beneficioOrigemId, Long beneficioDestinoId, BigDecimal valor);
}
