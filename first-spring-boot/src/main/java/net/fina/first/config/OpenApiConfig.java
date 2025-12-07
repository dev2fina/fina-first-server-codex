package net.fina.first.config;

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

/**
 * OpenAPI (Swagger) configuration for API documentation.
 */
@Configuration
public class OpenApiConfig {

    @Value("${server.servlet.context-path:/api}")
    private String contextPath;

    @Bean
    public OpenAPI customOpenAPI() {
        final String securitySchemeName = "bearerAuth";

        return new OpenAPI()
                .info(new Info()
                        .title("FIRST API - Financial Institution Registry System")
                        .version("1.0.0")
                        .description("""
                                REST API for the FIRST (Financial Institution Registry System) application.

                                This API provides endpoints for managing:
                                - Financial Institution Registration
                                - Branches Management
                                - Administrators Management
                                - Beneficiaries Management
                                - Licenses Management
                                - Workflow Actions
                                - Document Management
                                - User Authentication and Authorization
                                """)
                        .contact(new Contact()
                                .name("FINA Development Team")
                                .email("support@fina.net")
                                .url("https://www.fina.net"))
                        .license(new License()
                                .name("Proprietary")
                                .url("https://www.fina.net/license")))
                .servers(List.of(
                        new Server()
                                .url("http://localhost:8080" + contextPath)
                                .description("Development Server"),
                        new Server()
                                .url("https://api.fina.net" + contextPath)
                                .description("Production Server")))
                .addSecurityItem(new SecurityRequirement().addList(securitySchemeName))
                .components(new Components()
                        .addSecuritySchemes(securitySchemeName,
                                new SecurityScheme()
                                        .name(securitySchemeName)
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                                        .description("JWT token for authentication")));
    }
}
