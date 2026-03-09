package com.example.backend.adapters.in.web.dto;

import java.math.BigDecimal;

public record BeneficioResponse(
        Long id,
        String nome,
        String descricao,
        BigDecimal valor,
        boolean ativo,
        Long version
) {
}
