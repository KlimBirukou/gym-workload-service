package com.epam.gym.workload.configuration;

import com.epam.gym.workload.configuration.properties.SecurityProperties;
import com.epam.gym.workload.configuration.properties.SwaggerProperties;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
@RequiredArgsConstructor
public class SwaggerConfiguration {

    private final SwaggerProperties swaggerProperties;
    private final SecurityProperties securityProperties;

    @Bean
    public GroupedOpenApi publicApi() {
        return GroupedOpenApi.builder()
            .group("public-api")
            .pathsToMatch(securityProperties.publicPrefix() + "/**")
            .build();
    }

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
            .info(buildApiInfo())
            .servers(buildServers())
            .components(new Components().addSecuritySchemes("bearerAuth", buildSecurityScheme()))
            .addSecurityItem(new SecurityRequirement().addList("bearerAuth"));
    }

    private Info buildApiInfo() {
        return new Info()
            .title("Gym workload server API")
            .description("REST-API description with examples")
            .version("1.0.0");
    }

    private List<Server> buildServers() {
        return List.of(
            new Server()
                .url(swaggerProperties.serverUrl())
                .description(swaggerProperties.serverDescription())
        );
    }

    private SecurityScheme buildSecurityScheme() {
        return new SecurityScheme()
            .type(SecurityScheme.Type.HTTP)
            .scheme("bearer")
            .bearerFormat("JWT");
    }
}
