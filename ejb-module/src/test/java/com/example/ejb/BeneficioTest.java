package com.example.ejb;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigDecimal;
import org.junit.jupiter.api.Test;

class BeneficioTest {

    @Test
    void gettersESettersDevemManterEstadoDaEntidade() {
        Beneficio beneficio = new Beneficio();

        beneficio.setId(10L);
        beneficio.setNome("Plano Corporativo");
        beneficio.setDescricao("Beneficio de saude");
        beneficio.setValor(new BigDecimal("199.99"));
        beneficio.setAtivo(Boolean.TRUE);
        beneficio.setVersion(7L);

        assertEquals(10L, beneficio.getId());
        assertEquals("Plano Corporativo", beneficio.getNome());
        assertEquals("Beneficio de saude", beneficio.getDescricao());
        assertEquals(new BigDecimal("199.99"), beneficio.getValor());
        assertEquals(Boolean.TRUE, beneficio.getAtivo());
        assertEquals(7L, beneficio.getVersion());
    }
}
