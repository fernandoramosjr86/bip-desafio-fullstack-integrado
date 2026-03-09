package com.example.backend.domain.model;

import com.example.backend.domain.exception.RegraNegocioException;
import java.math.BigDecimal;

public record Beneficio(
        Long id,
        String nome,
        String descricao,
        BigDecimal valor,
        boolean ativo,
        Long version
) {

    private static final int NOME_MAX_LENGTH = 100;
    private static final int DESCRICAO_MAX_LENGTH = 255;

    public Beneficio {
        nome = normalizarNome(nome);
        descricao = normalizarDescricao(descricao);
        valor = validarValor(valor);
    }

    public static Beneficio novo(String nome, String descricao, BigDecimal valor, Boolean ativo) {
        return new Beneficio(null, nome, descricao, valor, ativo == null || ativo, null);
    }

    public Beneficio atualizarCom(String nome, String descricao, BigDecimal valor, Boolean ativo) {
        return new Beneficio(
                id,
                nome,
                descricao,
                valor,
                ativo == null ? this.ativo : ativo,
                version
        );
    }

    private static String normalizarNome(String nome) {
        if (nome == null || nome.isBlank()) {
            throw new RegraNegocioException("Nome do beneficio e obrigatorio");
        }
        String nomeNormalizado = nome.trim();
        if (nomeNormalizado.length() > NOME_MAX_LENGTH) {
            throw new RegraNegocioException("Nome do beneficio deve ter no maximo 100 caracteres");
        }
        return nomeNormalizado;
    }

    private static String normalizarDescricao(String descricao) {
        if (descricao == null) {
            return null;
        }
        String descricaoNormalizada = descricao.trim();
        if (descricaoNormalizada.length() > DESCRICAO_MAX_LENGTH) {
            throw new RegraNegocioException("Descricao do beneficio deve ter no maximo 255 caracteres");
        }
        return descricaoNormalizada.isEmpty() ? null : descricaoNormalizada;
    }

    private static BigDecimal validarValor(BigDecimal valor) {
        if (valor == null || valor.compareTo(BigDecimal.ZERO) <= 0) {
            throw new RegraNegocioException("Valor do beneficio deve ser maior que zero");
        }
        return valor;
    }
}
