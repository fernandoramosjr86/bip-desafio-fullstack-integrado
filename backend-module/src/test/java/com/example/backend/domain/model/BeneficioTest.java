package com.example.backend.domain.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.example.backend.domain.exception.RegraNegocioException;
import java.math.BigDecimal;
import org.junit.jupiter.api.Test;

class BeneficioTest {

    @Test
    void deveCriarBeneficioQuandoDadosObrigatoriosForemInformados() {
        Beneficio beneficio = new Beneficio(1L, " Nome ", " Desc ", BigDecimal.ONE, true, 0L);

        assertEquals("Nome", beneficio.nome());
        assertEquals("Desc", beneficio.descricao());
        assertEquals(BigDecimal.ONE, beneficio.valor());
    }

    @Test
    void deveFalharQuandoNomeForNuloOuInvalido() {
        RegraNegocioException ex = assertThrows(
                RegraNegocioException.class,
                () -> new Beneficio(1L, null, "Desc", BigDecimal.ONE, true, 0L)
        );

        assertEquals("Nome do beneficio e obrigatorio", ex.getMessage());

        RegraNegocioException ex2 = assertThrows(
                RegraNegocioException.class,
                () -> new Beneficio(1L, "   ", "Desc", BigDecimal.ONE, true, 0L)
        );

        assertEquals("Nome do beneficio e obrigatorio", ex2.getMessage());
    }

    @Test
    void deveFalharQuandoValorForNuloOuMenorOuIgualAZero() {
        RegraNegocioException ex = assertThrows(
                RegraNegocioException.class,
                () -> new Beneficio(1L, "Nome", "Desc", null, true, 0L)
        );

        assertEquals("Valor do beneficio deve ser maior que zero", ex.getMessage());

        RegraNegocioException ex2 = assertThrows(
                RegraNegocioException.class,
                () -> new Beneficio(1L, "Nome", "Desc", BigDecimal.ZERO, true, 0L)
        );

        assertEquals("Valor do beneficio deve ser maior que zero", ex2.getMessage());
    }

    @Test
    void deveFalharQuandoNomeOuDescricaoExcederemLimites() {
        RegraNegocioException exNome = assertThrows(
                RegraNegocioException.class,
                () -> new Beneficio(1L, "N".repeat(101), "Desc", BigDecimal.ONE, true, 0L)
        );

        assertEquals("Nome do beneficio deve ter no maximo 100 caracteres", exNome.getMessage());

        RegraNegocioException exDescricao = assertThrows(
                RegraNegocioException.class,
                () -> new Beneficio(1L, "Nome", "D".repeat(256), BigDecimal.ONE, true, 0L)
        );

        assertEquals("Descricao do beneficio deve ter no maximo 255 caracteres", exDescricao.getMessage());
    }

    @Test
    void deveCriarNovoEBeneficioAtualizadoComRegrasDeDominio() {
        Beneficio novo = Beneficio.novo(" Nome ", " Desc ", new BigDecimal("10.00"), null);

        assertEquals("Nome", novo.nome());
        assertEquals("Desc", novo.descricao());
        assertEquals(true, novo.ativo());

        Beneficio atualizado = new Beneficio(5L, "Antigo", "Texto", new BigDecimal("1.00"), false, 2L)
                .atualizarCom(" Novo ", "   ", new BigDecimal("2.00"), null);

        assertEquals(5L, atualizado.id());
        assertEquals("Novo", atualizado.nome());
        assertEquals(null, atualizado.descricao());
        assertEquals(new BigDecimal("2.00"), atualizado.valor());
        assertEquals(false, atualizado.ativo());
        assertEquals(2L, atualizado.version());
    }
}
