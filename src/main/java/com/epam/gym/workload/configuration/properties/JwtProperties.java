package com.epam.gym.workload.configuration.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "application.jwt")
public record JwtProperties(
    String authHeader,
    String bearerPrefix
) {

}
