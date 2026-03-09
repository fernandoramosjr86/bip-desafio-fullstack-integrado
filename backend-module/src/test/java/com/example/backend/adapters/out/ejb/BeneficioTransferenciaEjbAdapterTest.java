package com.example.backend.adapters.out.ejb;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.example.backend.adapters.out.ejb.integration.EjbTransferenciaClient;
import com.example.backend.adapters.out.ejb.integration.EjbTransferenciaNaoEncontradaException;
import com.example.backend.adapters.out.ejb.integration.EjbTransferenciaRegraException;
import com.example.backend.domain.exception.BeneficioNaoEncontradoException;
import com.example.backend.domain.exception.RegraNegocioException;
import java.math.BigDecimal;
import org.junit.jupiter.api.Test;

class BeneficioTransferenciaEjbAdapterTest {

    @Test
    void deveMapearErroDeNaoEncontradoParaDominio() {
        StubEjbTransferenciaClient client = new StubEjbTransferenciaClient();
        client.exceptionToThrow = new EjbTransferenciaNaoEncontradaException(1L);
        BeneficioTransferenciaEjbAdapter adapter = new BeneficioTransferenciaEjbAdapter(client);

        BeneficioNaoEncontradoException ex = assertThrows(
                BeneficioNaoEncontradoException.class,
                () -> adapter.transferir(1L, 2L, BigDecimal.ONE)
        );

        assertEquals("Beneficio nao encontrado para id=1", ex.getMessage());
    }

    @Test
    void deveMapearErroDeRegraParaDominio() {
        StubEjbTransferenciaClient client = new StubEjbTransferenciaClient();
        client.exceptionToThrow = new EjbTransferenciaRegraException("Saldo insuficiente para transferencia", null);
        BeneficioTransferenciaEjbAdapter adapter = new BeneficioTransferenciaEjbAdapter(client);

        RegraNegocioException ex = assertThrows(
                RegraNegocioException.class,
                () -> adapter.transferir(1L, 2L, BigDecimal.ONE)
        );

        assertEquals("Saldo insuficiente para transferencia", ex.getMessage());
    }

    @Test
    void deveDelegarTransferenciaQuandoNaoHaErro() {
        StubEjbTransferenciaClient client = new StubEjbTransferenciaClient();
        BeneficioTransferenciaEjbAdapter adapter = new BeneficioTransferenciaEjbAdapter(client);

        adapter.transferir(1L, 2L, BigDecimal.ONE);

        assertEquals(1L, client.lastFromId);
        assertEquals(2L, client.lastToId);
        assertEquals(BigDecimal.ONE, client.lastAmount);
    }

    private static final class StubEjbTransferenciaClient implements EjbTransferenciaClient {
        private RuntimeException exceptionToThrow;
        private Long lastFromId;
        private Long lastToId;
        private BigDecimal lastAmount;

        @Override
        public void transferir(Long beneficioOrigemId, Long beneficioDestinoId, BigDecimal valor) {
            this.lastFromId = beneficioOrigemId;
            this.lastToId = beneficioDestinoId;
            this.lastAmount = valor;

            if (exceptionToThrow != null) {
                throw exceptionToThrow;
            }
        }
    }
}
