package com.example.backend.application.port.in.command;

import java.math.BigDecimal;

public record CriarBeneficioCommand(
        String nome,
        String descricao,
        BigDecimal valor,
        Boolean ativo
) {
}
