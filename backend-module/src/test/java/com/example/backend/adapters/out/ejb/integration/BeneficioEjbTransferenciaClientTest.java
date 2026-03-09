package com.example.backend.adapters.out.ejb.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.example.ejb.BeneficioEjbService;
import com.example.ejb.exception.BeneficioEjbNaoEncontradoException;
import com.example.ejb.exception.TransferenciaEjbInvalidaException;
import java.math.BigDecimal;
import org.junit.jupiter.api.Test;

class BeneficioEjbTransferenciaClientTest {

    @Test
    void deveMapearErroDeNaoEncontradoDoEjb() {
        StubBeneficioEjbService service = new StubBeneficioEjbService();
        service.exceptionToThrow = new BeneficioEjbNaoEncontradoException(9L);
        BeneficioEjbTransferenciaClient client = new BeneficioEjbTransferenciaClient(service);

        EjbTransferenciaNaoEncontradaException ex = assertThrows(
                EjbTransferenciaNaoEncontradaException.class,
                () -> client.transferir(1L, 2L, BigDecimal.ONE)
        );

        assertEquals(9L, ex.getBeneficioId());
    }

    @Test
    void deveMapearErroDeRegraDoEjb() {
        StubBeneficioEjbService service = new StubBeneficioEjbService();
        service.exceptionToThrow = new TransferenciaEjbInvalidaException("Saldo insuficiente para transferencia");
        BeneficioEjbTransferenciaClient client = new BeneficioEjbTransferenciaClient(service);

        EjbTransferenciaRegraException ex = assertThrows(
                EjbTransferenciaRegraException.class,
                () -> client.transferir(1L, 2L, BigDecimal.ONE)
        );

        assertEquals("Saldo insuficiente para transferencia", ex.getMessage());
    }

    @Test
    void deveDelegarTransferenciaParaServicoEjb() {
        StubBeneficioEjbService service = new StubBeneficioEjbService();
        BeneficioEjbTransferenciaClient client = new BeneficioEjbTransferenciaClient(service);

        client.transferir(10L, 20L, new BigDecimal("7.50"));

        assertEquals(10L, service.lastFromId);
        assertEquals(20L, service.lastToId);
        assertEquals(new BigDecimal("7.50"), service.lastAmount);
    }

    private static final class StubBeneficioEjbService extends BeneficioEjbService {
        private RuntimeException exceptionToThrow;
        private Long lastFromId;
        private Long lastToId;
        private BigDecimal lastAmount;

        @Override
        public void transfer(Long fromId, Long toId, BigDecimal amount) {
            this.lastFromId = fromId;
            this.lastToId = toId;
            this.lastAmount = amount;

            if (exceptionToThrow != null) {
                throw exceptionToThrow;
            }
        }
    }
}
