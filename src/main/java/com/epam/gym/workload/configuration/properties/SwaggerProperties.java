package com.epam.gym.workload.configuration.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "application.swagger")
public record SwaggerProperties(
    String serverUrl,
    String serverDescription
) {

}
