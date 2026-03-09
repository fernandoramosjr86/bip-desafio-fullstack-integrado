package com.example.backend.adapters.out.ejb.integration;

import com.example.ejb.BeneficioEjbService;
import com.example.ejb.exception.BeneficioEjbNaoEncontradoException;
import com.example.ejb.exception.TransferenciaEjbInvalidaException;
import java.math.BigDecimal;
import org.springframework.stereotype.Component;

@Component
public class BeneficioEjbTransferenciaClient implements EjbTransferenciaClient {

    private final BeneficioEjbService beneficioEjbService;

    public BeneficioEjbTransferenciaClient(BeneficioEjbService beneficioEjbService) {
        this.beneficioEjbService = beneficioEjbService;
    }

    @Override
    public void transferir(Long beneficioOrigemId, Long beneficioDestinoId, BigDecimal valor) {
        try {
            beneficioEjbService.transfer(beneficioOrigemId, beneficioDestinoId, valor);
        } catch (BeneficioEjbNaoEncontradoException ex) {
            throw new EjbTransferenciaNaoEncontradaException(ex.getBeneficioId());
        } catch (TransferenciaEjbInvalidaException ex) {
            throw new EjbTransferenciaRegraException(ex.getMessage(), ex);
        }
    }
}
