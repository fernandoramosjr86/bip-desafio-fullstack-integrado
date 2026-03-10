package com.example.backend.adapters.out.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.Instant;

@Entity(name = "BackendTransferenciaHistorico")
@Table(name = "TRANSFERENCIA_HISTORICO")
public class TransferenciaHistoricoJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @Column(name = "BENEFICIO_ORIGEM_ID", nullable = false)
    private Long beneficioOrigemId;

    @Column(name = "BENEFICIO_DESTINO_ID", nullable = false)
    private Long beneficioDestinoId;

    @Column(name = "VALOR", nullable = false, precision = 15, scale = 2)
    private BigDecimal valor;

    @Column(name = "EXECUTADO_EM", nullable = false)
    private Instant executadoEm;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getBeneficioOrigemId() {
        return beneficioOrigemId;
    }

    public void setBeneficioOrigemId(Long beneficioOrigemId) {
        this.beneficioOrigemId = beneficioOrigemId;
    }

    public Long getBeneficioDestinoId() {
        return beneficioDestinoId;
    }

    public void setBeneficioDestinoId(Long beneficioDestinoId) {
        this.beneficioDestinoId = beneficioDestinoId;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public Instant getExecutadoEm() {
        return executadoEm;
    }

    public void setExecutadoEm(Instant executadoEm) {
        this.executadoEm = executadoEm;
    }
}
