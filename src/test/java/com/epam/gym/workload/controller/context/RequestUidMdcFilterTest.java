package com.epam.gym.workload.controller.context;

import com.epam.gym.workload.configuration.properties.RequestUidProperties;
import jakarta.servlet.FilterChain;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.MDC;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

@ExtendWith(MockitoExtension.class)
class RequestUidMdcFilterTest {

    private static final String MDC_KEY = "mdc_key";
    private static final String HEADER_NAME = "header_name";

    @Mock
    private RequestUidProperties requestUidProperties;

    @InjectMocks
    private RequestUidMdcFilter testObject;

    @BeforeEach
    void setUp() {
        lenient().doReturn(MDC_KEY).when(requestUidProperties).mdcKey();
        lenient().doReturn(HEADER_NAME).when(requestUidProperties).headerName();
    }

    @AfterEach
    void tearDown() {
        MDC.clear();
        verifyNoMoreInteractions(requestUidProperties);
    }

    @Test
    void doFilterInternal_shouldUseUidFromHeader_whenHeaderIsPresent() throws Exception {
        var existingUid = UUID.randomUUID().toString();
        var request = new MockHttpServletRequest();
        request.addHeader(HEADER_NAME, existingUid);
        var response = new MockHttpServletResponse();
        var chain = mock(FilterChain.class);

        testObject.doFilterInternal(request, response, chain);

        assertEquals(existingUid, response.getHeader(HEADER_NAME));
        verify(requestUidProperties, times(2)).headerName();
        verify(requestUidProperties).mdcKey();
        verify(chain).doFilter(request, response);
    }

    @Test
    void doFilterInternal_shouldGenerateUid_whenHeaderIsMissing() throws Exception {
        var request = new MockHttpServletRequest();
        var response = new MockHttpServletResponse();
        var chain = mock(FilterChain.class);

        testObject.doFilterInternal(request, response, chain);

        assertNotNull(response.getHeader(HEADER_NAME));
        verify(requestUidProperties, times(2)).headerName();
        verify(requestUidProperties).mdcKey();
        verify(chain).doFilter(request, response);
    }

    @Test
    void doFilterInternal_shouldGenerateUid_whenHeaderIsBlank() throws Exception {
        var request = new MockHttpServletRequest();
        request.addHeader(HEADER_NAME, "   ");
        var response = new MockHttpServletResponse();
        var chain = mock(FilterChain.class);

        testObject.doFilterInternal(request, response, chain);

        assertNotNull(response.getHeader(HEADER_NAME));
        verify(requestUidProperties, times(2)).headerName();
        verify(requestUidProperties).mdcKey();
        verify(chain).doFilter(request, response);
    }
}
