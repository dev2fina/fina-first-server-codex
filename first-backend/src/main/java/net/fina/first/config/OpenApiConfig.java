package net.fina.first.config;

import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI firstApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("FINA FIRST API")
                        .description("Modernized FIRST module API")
                        .version("1.0.0"))
                .externalDocs(new ExternalDocumentation()
                        .description("FINA FIRST Documentation")
                        .url("https://example.org/docs"));
    }
}
