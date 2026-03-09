package com.example.backend.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfiguration {

    @Bean
    public OpenAPI backendOpenApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("Beneficios API")
                        .description("API REST para CRUD e transferencia de beneficios")
                        .version("v1")
                        .contact(new Contact()
                                .name("Equipe Backend")
                                .email("backend@example.com"))
                        .license(new License()
                                .name("MIT")
                                .url("https://opensource.org/licenses/MIT")));
    }
}
