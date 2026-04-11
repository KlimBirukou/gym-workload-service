package com.epam.gym.workload.controller.context;

import com.epam.gym.workload.client.IAuthClient;
import com.epam.gym.workload.client.ValidateResponse;
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
import java.util.List;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthentificationFilter extends OncePerRequestFilter {

    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";

    private final IAuthClient authClient;
    private final SecurityProperties securityProperties;

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return !request
            .getRequestURI()
            .startsWith(securityProperties.publicPrefix());
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {
        Optional.ofNullable(request.getHeader(AUTHORIZATION_HEADER))
            .filter(header -> header.startsWith(BEARER_PREFIX))
            .flatMap(this::safeValidate)
            .filter(ValidateResponse::valid)
            .ifPresent(res -> authenticateUser(res.username()));
        filterChain.doFilter(request, response);
    }

    private Optional<ValidateResponse> safeValidate(String header) {
        try {
            return Optional.ofNullable(authClient.validate(header));
        } catch (Exception e) {
            log.error("Auth validation failed: {}", e.getMessage());
            return Optional.empty();
        }
    }

    private void authenticateUser(String username) {
        var authentication = new UsernamePasswordAuthenticationToken(
            username,
            null,
            List.of()
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);
        log.debug("JWT authenticated: username={}", username);
    }
}
