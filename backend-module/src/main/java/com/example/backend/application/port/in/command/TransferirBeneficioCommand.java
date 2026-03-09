package com.example.backend.application.port.in.command;

import java.math.BigDecimal;

public record TransferirBeneficioCommand(
        Long beneficioOrigemId,
        Long beneficioDestinoId,
        BigDecimal valor
) {
}
