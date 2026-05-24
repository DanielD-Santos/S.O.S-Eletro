package com.soseletro.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Value("${server.port:8080}")
    private int serverPort;

    @Bean
    public OpenAPI sosEletroOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("S.O.S Eletro API")
                        .description("API REST para gestão de assistência técnica em eletrônicos. " +
                                "Esta API oferece funcionalidades completas para gerenciar agendamentos, " +
                                "clientes, protocolos, técnicos e autenticação de funcionários.")
                        .version("v1.0.0")
                        .contact(new Contact()
                                .name("S.O.S Eletro")
                                .email("contato@soseletro.local"))
                        .license(new License()
                                .name("Projeto Acadêmico")
                                .url("https://github.com/soseletro")))
                .servers(List.of(
                        new Server()
                                .url("http://localhost:" + serverPort)
                                .description("Servidor de Desenvolvimento Local"),
                        new Server()
                                .url("https://api.soseletro.com")
                                .description("Servidor de Produção (exemplo)")))
                .addSecurityItem(new SecurityRequirement().addList("Bearer Authentication"))
                .components(new Components()
                        .addSecuritySchemes("Bearer Authentication", createAPIKeyScheme()));
    }

    private SecurityScheme createAPIKeyScheme() {
        return new SecurityScheme()
                .type(SecurityScheme.Type.HTTP)
                .scheme("bearer")
                .bearerFormat("JWT")
                .in(SecurityScheme.In.HEADER)
                .name("Authorization")
                .description("Token JWT obtido através do endpoint de login (/api/v1/auth/login). " +
                        "Formato: 'Bearer {token}'");
    }
}