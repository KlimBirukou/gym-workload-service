package com.epam.gym.workload.controller.context;

import com.epam.gym.workload.configuration.properties.InternalProperties;
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
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

@ExtendWith(MockitoExtension.class)
class InternalSecretFilterTest {

    private static final String INTERNAL_PREFIX = "internal_prefix";
    private static final String INTERNAL_URI = "internal_uri";
    private static final String PUBLIC_URI = "public_uri";
    private static final String SECRET_HEADER = "secret_header";
    private static final String SECRET = "secret";
    private static final String WRONG_SECRET = "wrong_secret";
    private static final String PRINCIPAL = "internal-service";

    @Mock
    private InternalProperties internalProperties;
    @Mock
    private SecurityProperties securityProperties;
    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpServletResponse response;
    @Mock
    private FilterChain filterChain;

    @InjectMocks
    private InternalSecretFilter testObject;

    @BeforeEach
    void setUp() {
        SecurityContextHolder.clearContext();
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
        verifyNoMoreInteractions(internalProperties, securityProperties, filterChain);
    }

    @Test
    void shouldNotFilter_shouldReturnTrue_whenUriDoesNotStartWithInternalPrefix() {
        doReturn(INTERNAL_PREFIX).when(securityProperties).internalPrefix();
        doReturn(PUBLIC_URI).when(request).getRequestURI();

        var result = testObject.shouldNotFilter(request);

        assertTrue(result);
        verify(securityProperties).internalPrefix();
    }

    @Test
    void doFilterInternal_shouldAuthenticate_whenSecretIsValid() throws ServletException, IOException {
        doReturn(SECRET_HEADER).when(internalProperties).headerName();
        doReturn(SECRET).when(internalProperties).secret();
        doReturn(PRINCIPAL).when(internalProperties).principal();
        doReturn(SECRET).when(request).getHeader(SECRET_HEADER);

        testObject.doFilterInternal(request, response, filterChain);

        var authentication = SecurityContextHolder.getContext().getAuthentication();
        assertNotNull(authentication);
        assertEquals(PRINCIPAL, authentication.getPrincipal());
        verify(internalProperties).headerName();
        verify(internalProperties).secret();
        verify(internalProperties).principal();
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void doFilterInternal_shouldNotAuthenticate_whenSecretIsWrong() throws ServletException, IOException {
        doReturn(SECRET_HEADER).when(internalProperties).headerName();
        doReturn(SECRET).when(internalProperties).secret();
        doReturn(INTERNAL_URI).when(request).getRequestURI();
        doReturn(WRONG_SECRET).when(request).getHeader(SECRET_HEADER);

        testObject.doFilterInternal(request, response, filterChain);

        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(internalProperties).headerName();
        verify(internalProperties).secret();
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void doFilterInternal_shouldNotAuthenticate_whenSecretHeaderMissing() throws ServletException, IOException {
        doReturn(SECRET_HEADER).when(internalProperties).headerName();
        doReturn(SECRET).when(internalProperties).secret();
        doReturn(INTERNAL_URI).when(request).getRequestURI();
        doReturn(null).when(request).getHeader(SECRET_HEADER);

        testObject.doFilterInternal(request, response, filterChain);

        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(internalProperties).headerName();
        verify(internalProperties).secret();
        verify(filterChain).doFilter(request, response);
    }
}
