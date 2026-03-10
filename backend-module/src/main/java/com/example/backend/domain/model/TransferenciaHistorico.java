package com.example.backend.domain.model;

import com.example.backend.domain.exception.RegraNegocioException;
import java.math.BigDecimal;
import java.time.Instant;

public record TransferenciaHistorico(
        Long id,
        Long beneficioOrigemId,
        Long beneficioDestinoId,
        BigDecimal valor,
        Instant executadoEm
) {

    public static TransferenciaHistorico nova(
            Long beneficioOrigemId,
            Long beneficioDestinoId,
            BigDecimal valor,
            Instant executadoEm
    ) {
        if (beneficioOrigemId == null || beneficioDestinoId == null) {
            throw new RegraNegocioException("Ids de origem e destino do historico sao obrigatorios");
        }
        if (beneficioOrigemId.equals(beneficioDestinoId)) {
            throw new RegraNegocioException("Historico invalido: origem e destino devem ser diferentes");
        }
        if (valor == null || valor.compareTo(BigDecimal.ZERO) <= 0) {
            throw new RegraNegocioException("Historico invalido: valor da transferencia deve ser maior que zero");
        }
        if (executadoEm == null) {
            throw new RegraNegocioException("Historico invalido: instante de execucao e obrigatorio");
        }
        return new TransferenciaHistorico(null, beneficioOrigemId, beneficioDestinoId, valor, executadoEm);
    }
}
