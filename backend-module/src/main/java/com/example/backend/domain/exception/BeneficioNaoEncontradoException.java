package com.example.backend.domain.exception;

public class BeneficioNaoEncontradoException extends RuntimeException {
    public BeneficioNaoEncontradoException(Long id) {
        super("Beneficio nao encontrado para id=" + id);
    }
}
