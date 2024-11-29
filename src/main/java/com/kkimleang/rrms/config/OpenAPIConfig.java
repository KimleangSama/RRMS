package com.kkimleang.rrms.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenAPIConfig {
    String schemeName = "bearerAuth";
    String bearerFormat = "JWT";
    String scheme = "bearer";

    @Bean
    public OpenAPI orderServiceAPI() {
        return new OpenAPI()
                .info(new Info().title("RRMS Backend API")
                        .description("This is the REST API for RRMS Backend")
                        .version("v0.1")
                        .license(new License().name("Apache 2.0")))
                .externalDocs(new ExternalDocumentation()
                        .description("You can refer to the Auth Service wiki documentation")
                        .url("https://RRMS.kkimleang.com"))
                .addSecurityItem(new SecurityRequirement().addList(schemeName))
                .components(new Components()
                        .addSecuritySchemes(schemeName, new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .scheme(scheme)
                                .bearerFormat(bearerFormat).in(SecurityScheme.In.HEADER)
                                .name("Authorization")
                        )
                );
    }
}