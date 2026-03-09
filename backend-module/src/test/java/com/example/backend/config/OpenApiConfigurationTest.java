package com.example.backend.config;

import static org.junit.jupiter.api.Assertions.assertEquals;

import io.swagger.v3.oas.models.OpenAPI;
import org.junit.jupiter.api.Test;

class OpenApiConfigurationTest {

    @Test
    void backendOpenApiDeveConfigurarMetadadosEsperados() {
        OpenApiConfiguration configuration = new OpenApiConfiguration();

        OpenAPI openAPI = configuration.backendOpenApi();

        assertEquals("Beneficios API", openAPI.getInfo().getTitle());
        assertEquals("v1", openAPI.getInfo().getVersion());
        assertEquals("Equipe Backend", openAPI.getInfo().getContact().getName());
        assertEquals("MIT", openAPI.getInfo().getLicense().getName());
    }
}

