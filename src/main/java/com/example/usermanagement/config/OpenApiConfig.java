package com.example.usermanagement.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * springdoc-openapi configuration that publishes API metadata at
 * {@code /api-docs} and the interactive Swagger UI at {@code /swagger-ui.html}.
 */
@Configuration
public class OpenApiConfig {

    /**
     * Builds the OpenAPI document metadata.
     *
     * @return configured {@link OpenAPI} bean
     */
    @Bean
    public OpenAPI userManagementOpenApi() {
        return new OpenAPI().info(new Info()
                .title("User Management API")
                .version("1.0.0")
                .description("REST API for managing application users.")
                .contact(new Contact().name("User Management Team").email("dev@example.com"))
                .license(new License().name("Apache 2.0").url("https://www.apache.org/licenses/LICENSE-2.0")));
    }
}
