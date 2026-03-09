package com.example.backend.adapters.in.web.dto;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import java.math.BigDecimal;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class RequestValidationTest {

    private static Validator validator;

    @BeforeAll
    static void setup() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    @Test
    void beneficioRequestInvalidoDeveGerarViolacoes() {
        BeneficioRequest request = new BeneficioRequest("", "desc", BigDecimal.ZERO, true);

        var violations = validator.validate(request);

        assertFalse(violations.isEmpty());
    }

    @Test
    void beneficioRequestValidoNaoDeveGerarViolacoes() {
        BeneficioRequest request = new BeneficioRequest("Nome", "desc", new BigDecimal("1.00"), null);

        var violations = validator.validate(request);

        assertTrue(violations.isEmpty());
    }

    @Test
    void transferenciaRequestInvalidoDeveGerarViolacoes() {
        TransferenciaRequest request = new TransferenciaRequest(null, null, BigDecimal.ZERO);

        var violations = validator.validate(request);

        assertFalse(violations.isEmpty());
    }
}

