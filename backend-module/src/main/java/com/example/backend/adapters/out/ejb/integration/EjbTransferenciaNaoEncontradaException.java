package com.example.backend.adapters.out.ejb.integration;

public class EjbTransferenciaNaoEncontradaException extends RuntimeException {

    private final Long beneficioId;

    public EjbTransferenciaNaoEncontradaException(Long beneficioId) {
        super("Beneficio nao encontrado no EJB para id=" + beneficioId);
        this.beneficioId = beneficioId;
    }

    public Long getBeneficioId() {
        return beneficioId;
    }
}
