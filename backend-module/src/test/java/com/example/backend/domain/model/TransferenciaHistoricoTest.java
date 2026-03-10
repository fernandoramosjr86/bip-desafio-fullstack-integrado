package com.example.backend.domain.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.example.backend.domain.exception.RegraNegocioException;
import java.math.BigDecimal;
import java.time.Instant;
import org.junit.jupiter.api.Test;

class TransferenciaHistoricoTest {

    @Test
    void novaComDadosValidosDeveCriarHistoricoSemId() {
        Instant executadoEm = Instant.parse("2026-03-09T10:00:00Z");

        TransferenciaHistorico historico = TransferenciaHistorico.nova(
                1L,
                2L,
                new BigDecimal("12.50"),
                executadoEm
        );

        assertEquals(null, historico.id());
        assertEquals(1L, historico.beneficioOrigemId());
        assertEquals(2L, historico.beneficioDestinoId());
        assertEquals(new BigDecimal("12.50"), historico.valor());
        assertEquals(executadoEm, historico.executadoEm());
    }

    @Test
    void novaComIdsIguaisDeveFalhar() {
        RegraNegocioException ex = assertThrows(
                RegraNegocioException.class,
                () -> TransferenciaHistorico.nova(1L, 1L, BigDecimal.ONE, Instant.now())
        );

        assertEquals("Historico invalido: origem e destino devem ser diferentes", ex.getMessage());
    }
}
