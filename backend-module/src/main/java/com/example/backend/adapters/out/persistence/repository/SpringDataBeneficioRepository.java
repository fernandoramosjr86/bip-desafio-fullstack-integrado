package com.example.backend.adapters.out.persistence.repository;

import com.example.backend.adapters.out.persistence.entity.BeneficioJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SpringDataBeneficioRepository extends JpaRepository<BeneficioJpaEntity, Long> {
}
