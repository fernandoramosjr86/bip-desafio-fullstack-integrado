package com.example.backend.config;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.example.ejb.BeneficioEjbService;
import org.junit.jupiter.api.Test;

class EjbModuleConfigurationTest {

    @Test
    void deveCriarBeanDoServicoEjbComEntityManager() {
        EjbModuleConfiguration configuration = new EjbModuleConfiguration();
        BeneficioEjbService service = configuration.beneficioEjbService(null);

        assertNotNull(service);
    }
}
