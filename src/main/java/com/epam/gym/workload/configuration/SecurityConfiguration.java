package com.epam.gym.workload.configuration;

import com.epam.gym.workload.configuration.properties.PublicEndpointsProperties;
import com.epam.gym.workload.configuration.properties.SecurityProperties;
import com.epam.gym.workload.controller.context.InternalSecretFilter;
import com.epam.gym.workload.controller.context.JwtAuthentificationFilter;
import com.epam.gym.workload.controller.context.RequestUidMdcFilter;
import com.epam.gym.workload.exception.NotAuthenticatedException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfiguration {

    @Qualifier("handlerExceptionResolver")
    private final HandlerExceptionResolver handlerExceptionResolver;
    private final InternalSecretFilter internalSecretFilter;
    private final JwtAuthentificationFilter jwtAuthenticationFilter;
    private final RequestUidMdcFilter requestUidMdcFilter;
    private final SecurityProperties securityProperties;
    private final PublicEndpointsProperties publicEndpointsProperties;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
            .cors(AbstractHttpConfigurer::disable)
            .csrf(AbstractHttpConfigurer::disable)
            .sessionManagement(session ->
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(securityProperties.internalPrefix() + "/**").authenticated()
                .requestMatchers(securityProperties.publicPrefix() + "/**").authenticated()
                .requestMatchers(publicEndpointsProperties.publicEndpoints().toArray(String[]::new)).permitAll()
                .anyRequest().authenticated())
            .addFilterBefore(requestUidMdcFilter, UsernamePasswordAuthenticationFilter.class)
            .addFilterBefore(internalSecretFilter, UsernamePasswordAuthenticationFilter.class)
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
            .exceptionHandling(exception -> exception
                .authenticationEntryPoint(authenticationEntryPoint()))
            .build();
    }

    private AuthenticationEntryPoint authenticationEntryPoint() {
        return (request, response, exception) ->
            handlerExceptionResolver.resolveException(
                request, response, null,
                new NotAuthenticatedException()
            );
    }
}
