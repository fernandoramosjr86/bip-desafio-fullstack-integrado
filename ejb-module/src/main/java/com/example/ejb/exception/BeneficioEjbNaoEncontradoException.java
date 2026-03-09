package com.example.ejb.exception;

public class BeneficioEjbNaoEncontradoException extends RuntimeException {

    private final Long beneficioId;

    public BeneficioEjbNaoEncontradoException(Long beneficioId) {
        super("Beneficio nao encontrado para id=" + beneficioId);
        this.beneficioId = beneficioId;
    }

    public Long getBeneficioId() {
        return beneficioId;
    }
}
