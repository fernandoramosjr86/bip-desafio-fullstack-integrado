package com.example.backend.adapters.in.web.dto;

import java.math.BigDecimal;
import java.time.Instant;

public record TransferenciaHistoricoResponse(
        Long id,
        Long beneficioOrigemId,
        Long beneficioDestinoId,
        BigDecimal valor,
        Instant executadoEm
) {
}
