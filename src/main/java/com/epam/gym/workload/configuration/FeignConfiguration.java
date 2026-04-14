package com.epam.gym.workload.configuration;

import com.epam.gym.workload.configuration.properties.InternalProperties;
import com.epam.gym.workload.configuration.properties.RequestUidProperties;
import feign.RequestInterceptor;
import lombok.RequiredArgsConstructor;
import org.slf4j.MDC;
import org.springframework.context.annotation.Bean;

import java.util.Optional;

@RequiredArgsConstructor
public class FeignConfiguration {

    private final InternalProperties internalProperties;
    private final RequestUidProperties requestUidProperties;

    @Bean
    public RequestInterceptor internalSecretInterceptor() {
        return template -> template.header(
            internalProperties.headerName(),
            internalProperties.secret()
        );
    }

    @Bean
    public RequestInterceptor requestUidInterceptor() {
        return template -> Optional.ofNullable(MDC.get(requestUidProperties.mdcKey()))
            .ifPresent(uid -> template.header(requestUidProperties.headerName(), uid));
    }
}
