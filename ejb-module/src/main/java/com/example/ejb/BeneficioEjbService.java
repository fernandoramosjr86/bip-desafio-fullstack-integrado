package com.example.ejb;

import com.example.ejb.exception.BeneficioEjbNaoEncontradoException;
import com.example.ejb.exception.TransferenciaEjbInvalidaException;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;
import jakarta.persistence.PersistenceContext;
import java.math.BigDecimal;
import java.util.Objects;

@Stateless
public class BeneficioEjbService {

    @PersistenceContext
    private EntityManager em;

    public BeneficioEjbService() {
    }

    public BeneficioEjbService(EntityManager em) {
        this.em = em;
    }

    public void transfer(Long fromId, Long toId, BigDecimal amount) {
        Objects.requireNonNull(em, "EntityManager nao inicializado");
        validarEntrada(fromId, toId, amount);

        Beneficio from = em.find(Beneficio.class, fromId, LockModeType.PESSIMISTIC_WRITE);
        if (from == null) {
            throw new BeneficioEjbNaoEncontradoException(fromId);
        }

        Beneficio to = em.find(Beneficio.class, toId, LockModeType.PESSIMISTIC_WRITE);
        if (to == null) {
            throw new BeneficioEjbNaoEncontradoException(toId);
        }

        if (Boolean.FALSE.equals(from.getAtivo()) || Boolean.FALSE.equals(to.getAtivo())) {
            throw new TransferenciaEjbInvalidaException("Transferencia permitida apenas entre beneficios ativos");
        }
        if (from.getValor() == null || to.getValor() == null) {
            throw new TransferenciaEjbInvalidaException("Beneficios com valor invalido para transferencia");
        }
        if (from.getValor().compareTo(amount) < 0) {
            throw new TransferenciaEjbInvalidaException("Saldo insuficiente para transferencia");
        }

        from.setValor(from.getValor().subtract(amount));
        to.setValor(to.getValor().add(amount));

        em.merge(from);
        em.merge(to);
        em.flush();
    }

    private void validarEntrada(Long fromId, Long toId, BigDecimal amount) {
        if (fromId == null || toId == null) {
            throw new TransferenciaEjbInvalidaException("Ids de origem e destino sao obrigatorios");
        }
        if (fromId.equals(toId)) {
            throw new TransferenciaEjbInvalidaException("Origem e destino devem ser diferentes");
        }
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new TransferenciaEjbInvalidaException("Valor da transferencia deve ser maior que zero");
        }
    }
}
