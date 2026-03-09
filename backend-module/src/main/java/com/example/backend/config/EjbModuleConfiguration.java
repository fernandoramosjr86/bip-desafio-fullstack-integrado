package com.example.backend.config;

import com.example.ejb.BeneficioEjbService;
import jakarta.persistence.EntityManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class EjbModuleConfiguration {

    @Bean
    public BeneficioEjbService beneficioEjbService(EntityManager entityManager) {
        return new BeneficioEjbService(entityManager);
    }
}
