package com.vetSystem.vet_system.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI vetSystemOpenAPI() {
        return new OpenAPI().info(new Info()
                .title("API de Patitas Felices")
                .description("Gestión de dueños, mascotas, veterinarios y turnos de la clínica veterinaria.")
                .version("1.0.0"));
    }
}
