package com.epam.gym.workload.controller.context;

import com.epam.gym.workload.configuration.properties.RequestUidProperties;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class RequestUidMdcFilter extends OncePerRequestFilter {

    private final RequestUidProperties requestUidProperties;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {
        var uid = Optional.ofNullable(request.getHeader(requestUidProperties.headerName()))
            .filter(StringUtils::isNotBlank)
            .orElseGet(() -> UUID.randomUUID().toString());
        try (var ignored = MDC.putCloseable(requestUidProperties.mdcKey(), uid)) {
            response.addHeader(requestUidProperties.headerName(), uid);
            log.info("REST Request: {} {}", request.getMethod(), request.getRequestURI());
            filterChain.doFilter(request, response);
            log.info("REST Response: Status {}", response.getStatus());
        }
    }
}
