package com.epam.gym.workload.configuration.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "application.internal")
public record InternalProperties(
    String secret,
    String headerName,
    String principal
) {

}
