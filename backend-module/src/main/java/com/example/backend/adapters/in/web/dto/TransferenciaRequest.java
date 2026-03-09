package com.example.backend.adapters.in.web.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public record TransferenciaRequest(
        @NotNull(message = "beneficioOrigemId e obrigatorio")
        Long beneficioOrigemId,

        @NotNull(message = "beneficioDestinoId e obrigatorio")
        Long beneficioDestinoId,

        @NotNull(message = "valor e obrigatorio")
        @DecimalMin(value = "0.01", message = "valor deve ser maior que zero")
        BigDecimal valor
) {
}
