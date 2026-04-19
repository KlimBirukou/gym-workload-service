package com.epam.gym.workload.controller.context;

import com.epam.gym.workload.configuration.properties.InternalProperties;
import com.epam.gym.workload.configuration.properties.SecurityProperties;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

@Slf4j
@Component
@RequiredArgsConstructor
public class InternalSecretFilter extends OncePerRequestFilter {

    public static final String LOG_MESSAGE = "Rejected internal request without valid secret. URI={}";

    private final InternalProperties internalProperties;
    private final SecurityProperties securityProperties;

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return !request
            .getRequestURI()
            .startsWith(securityProperties.internalPrefix());
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {
        var requestSecret = request.getHeader(internalProperties.headerName());
        if (internalProperties.secret().equals(requestSecret)) {
            var auth = new UsernamePasswordAuthenticationToken(
                internalProperties.principal(), null, Collections.emptyList());
            SecurityContextHolder.getContext().setAuthentication(auth);
        } else {
            log.warn(LOG_MESSAGE, request.getRequestURI());
        }
        filterChain.doFilter(request, response);
    }
}
