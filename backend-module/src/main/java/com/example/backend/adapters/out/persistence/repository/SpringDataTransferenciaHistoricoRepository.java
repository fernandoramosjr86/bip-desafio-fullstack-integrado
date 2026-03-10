package com.example.backend.adapters.out.persistence.repository;

import com.example.backend.adapters.out.persistence.entity.TransferenciaHistoricoJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SpringDataTransferenciaHistoricoRepository extends JpaRepository<TransferenciaHistoricoJpaEntity, Long> {
}
