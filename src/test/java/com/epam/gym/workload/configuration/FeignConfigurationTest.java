package com.epam.gym.workload.configuration;

import com.epam.gym.workload.configuration.properties.InternalProperties;
import com.epam.gym.workload.configuration.properties.RequestUidProperties;
import feign.RequestTemplate;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.MDC;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

@ExtendWith(MockitoExtension.class)
class FeignConfigurationTest {

    private static final String INTERNAL_HEADER = "X-Internal-Secret";
    private static final String INTERNAL_SECRET = "secret";
    private static final String UID_HEADER = "X-Request-Id";
    private static final String MDC_KEY = "requestUid";
    private static final String UID_VALUE = "uid-value";

    @Mock
    private InternalProperties internalProperties;
    @Mock
    private RequestUidProperties requestUidProperties;

    @InjectMocks
    private FeignConfiguration testObject;

    @AfterEach
    void tearDown() {
        MDC.clear();
        verifyNoMoreInteractions(internalProperties, requestUidProperties);
    }

    @Test
    void internalSecretInterceptor_shouldAddSecretHeader() {
        doReturn(INTERNAL_HEADER).when(internalProperties).headerName();
        doReturn(INTERNAL_SECRET).when(internalProperties).secret();
        var template = mock(RequestTemplate.class);

        var interceptor = testObject.internalSecretInterceptor();
        assertNotNull(interceptor);
        interceptor.apply(template);

        verify(internalProperties).headerName();
        verify(internalProperties).secret();
        verify(template).header(INTERNAL_HEADER, INTERNAL_SECRET);
    }

    @Test
    void requestUidInterceptor_shouldAddUidHeader_whenMdcContainsUid() {
        MDC.put(MDC_KEY, UID_VALUE);
        doReturn(MDC_KEY).when(requestUidProperties).mdcKey();
        doReturn(UID_HEADER).when(requestUidProperties).headerName();
        var template = mock(RequestTemplate.class);

        var interceptor = testObject.requestUidInterceptor();
        assertNotNull(interceptor);
        interceptor.apply(template);

        verify(requestUidProperties).mdcKey();
        verify(requestUidProperties).headerName();
        verify(template).header(UID_HEADER, UID_VALUE);
    }

    @Test
    void requestUidInterceptor_shouldNotAddUidHeader_whenMdcIsEmpty() {
        doReturn(MDC_KEY).when(requestUidProperties).mdcKey();
        var template = mock(RequestTemplate.class);

        var interceptor = testObject.requestUidInterceptor();
        assertNotNull(interceptor);
        interceptor.apply(template);

        verify(requestUidProperties).mdcKey();
        verifyNoMoreInteractions(template);
    }
}
