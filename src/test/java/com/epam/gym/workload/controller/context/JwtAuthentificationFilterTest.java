package com.epam.gym.workload.controller.context;

import com.epam.gym.workload.client.IAuthClient;
import com.epam.gym.workload.client.ValidateResponse;
import com.epam.gym.workload.configuration.properties.SecurityProperties;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.context.SecurityContextHolder;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

@ExtendWith(MockitoExtension.class)
class JwtAuthentificationFilterTest {

    private static final String AUTH_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";
    private static final String TOKEN = "token";
    private static final String BEARER_TOKEN = BEARER_PREFIX + TOKEN;
    private static final String USERNAME = "username";
    private static final String PUBLIC_PREFIX = "public_prefix";

    @Mock
    private IAuthClient authClient;
    @Mock
    private SecurityProperties securityProperties;
    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpServletResponse response;
    @Mock
    private FilterChain filterChain;

    @InjectMocks
    private JwtAuthentificationFilter testObject;

    @BeforeEach
    void setUp() {
        SecurityContextHolder.clearContext();
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
        verifyNoMoreInteractions(authClient, securityProperties, filterChain);
    }

    @Test
    void shouldNotFilter_shouldReturnFalse_whenUriStartsWithPublicPrefix() {
        doReturn(PUBLIC_PREFIX).when(securityProperties).publicPrefix();
        doReturn(PUBLIC_PREFIX + "/trainers").when(request).getRequestURI();

        var result = testObject.shouldNotFilter(request);

        org.junit.jupiter.api.Assertions.assertFalse(result);
        verify(securityProperties).publicPrefix();
    }

    @Test
    void shouldNotFilter_shouldReturnTrue_whenUriDoesNotStartWithPublicPrefix() {
        doReturn(PUBLIC_PREFIX).when(securityProperties).publicPrefix();
        doReturn("/internal/v1/workload").when(request).getRequestURI();

        var result = testObject.shouldNotFilter(request);

        org.junit.jupiter.api.Assertions.assertTrue(result);
        verify(securityProperties).publicPrefix();
    }

    @Test
    void doFilterInternal_shouldAuthenticate_whenTokenIsValid() throws ServletException, IOException {
        var validateResponse = new ValidateResponse(true, USERNAME);

        doReturn(BEARER_TOKEN).when(request).getHeader(AUTH_HEADER);
        doReturn(validateResponse).when(authClient).validate(BEARER_TOKEN);

        testObject.doFilterInternal(request, response, filterChain);

        var authentication = SecurityContextHolder.getContext().getAuthentication();
        assertNotNull(authentication);
        assertEquals(USERNAME, authentication.getPrincipal());
        verify(authClient).validate(BEARER_TOKEN);
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void doFilterInternal_shouldNotAuthenticate_whenHeaderMissing() throws ServletException, IOException {
        doReturn(null).when(request).getHeader(AUTH_HEADER);

        testObject.doFilterInternal(request, response, filterChain);

        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void doFilterInternal_shouldNotAuthenticate_whenHeaderHasNoBearerPrefix() throws ServletException, IOException {
        doReturn("Basic dXNlcjpwYXNz").when(request).getHeader(AUTH_HEADER);

        testObject.doFilterInternal(request, response, filterChain);

        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void doFilterInternal_shouldNotAuthenticate_whenValidateResponseIsInvalid() throws ServletException, IOException {
        var validateResponse = new ValidateResponse(false, USERNAME);

        doReturn(BEARER_TOKEN).when(request).getHeader(AUTH_HEADER);
        doReturn(validateResponse).when(authClient).validate(BEARER_TOKEN);

        testObject.doFilterInternal(request, response, filterChain);

        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(authClient).validate(BEARER_TOKEN);
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void doFilterInternal_shouldNotAuthenticate_whenAuthClientThrowsException() throws ServletException, IOException {
        doReturn(BEARER_TOKEN).when(request).getHeader(AUTH_HEADER);
        doThrow(new RuntimeException("Connection refused")).when(authClient).validate(BEARER_TOKEN);

        testObject.doFilterInternal(request, response, filterChain);

        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(authClient).validate(BEARER_TOKEN);
        verify(filterChain).doFilter(request, response);
    }
}
