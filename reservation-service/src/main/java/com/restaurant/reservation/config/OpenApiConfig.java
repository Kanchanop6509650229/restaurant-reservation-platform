package com.restaurant.reservation.config;

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

import java.util.Arrays;

/**
 * Configuration class for OpenAPI documentation (Swagger).
 * Defines the API information, security schemes, and servers.
 * 
 * @author Restaurant Reservation Team
 * @version 1.0
 */
@Configuration
public class OpenApiConfig {

    /** Application name from properties */
    @Value("${spring.application.name:Reservation Service}")
    private String applicationName;
    
    /** Server port from properties */
    @Value("${server.port:8083}")
    private String serverPort;

    /**
     * Configures the OpenAPI documentation.
     * 
     * @return OpenAPI configuration
     */
    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(apiInfo())
                .servers(Arrays.asList(
                    new Server().url("http://localhost:" + serverPort).description("Local Development Server"),
                    new Server().url("https://api.restaurant-reservation.com").description("Production Server")
                ))
                .components(new Components()
                    .addSecuritySchemes("bearer-jwt", 
                        new SecurityScheme()
                            .type(SecurityScheme.Type.HTTP)
                            .scheme("bearer")
                            .bearerFormat("JWT")
                            .in(SecurityScheme.In.HEADER)
                            .name("Authorization")
                    )
                )
                .addSecurityItem(new SecurityRequirement().addList("bearer-jwt"));
    }

    /**
     * Configures the API information.
     * 
     * @return API information
     */
    private Info apiInfo() {
        return new Info()
                .title(applicationName + " API")
                .description("API documentation for the Reservation Service of the Restaurant Reservation Platform")
                .version("1.0")
                .contact(new Contact()
                    .name("Restaurant Reservation Team")
                    .email("support@restaurant-reservation.com")
                    .url("https://www.restaurant-reservation.com")
                )
                .license(new License()
                    .name("Apache 2.0")
                    .url("https://www.apache.org/licenses/LICENSE-2.0.html")
                );
    }
}
