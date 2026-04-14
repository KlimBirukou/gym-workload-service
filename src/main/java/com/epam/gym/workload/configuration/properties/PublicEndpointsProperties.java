package com.epam.gym.workload.configuration.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

@ConfigurationProperties(prefix = "application.security.endpoints")
public record PublicEndpointsProperties(
    List<String> publicEndpoints
) {

}
