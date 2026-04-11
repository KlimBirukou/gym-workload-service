package com.epam.gym.workload.configuration.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "application.auth")
public record AuthProperties(
    String url,
    String path
) {

}
