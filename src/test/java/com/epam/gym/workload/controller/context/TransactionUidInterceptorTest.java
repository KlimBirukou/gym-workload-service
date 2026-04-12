package com.epam.gym.workload.controller.context;

import com.epam.gym.workload.configuration.properties.RequestUidProperties;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.MDC;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.mock.http.client.MockClientHttpRequest;

import java.io.IOException;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

@ExtendWith(MockitoExtension.class)
class TransactionUidInterceptorTest {

    private static final String MDC_KEY = "mdc_key";
    private static final String HEADER_NAME = "header_name";

    @Mock
    private RequestUidProperties requestUidProperties;

    @InjectMocks
    private TransactionUidInterceptor testObject;

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
    void intercept_shouldAddUidHeader_whenMdcContainsUid() throws IOException {
        var uid = UUID.randomUUID().toString();
        MDC.put(MDC_KEY, uid);
        var request = new MockClientHttpRequest();
        var execution = mock(ClientHttpRequestExecution.class);

        testObject.intercept(request, new byte[0], execution);

        assertEquals(uid, request.getHeaders().getFirst(HEADER_NAME));
        verify(requestUidProperties).mdcKey();
        verify(requestUidProperties).headerName();
        verify(execution).execute(any(), any());
    }

    @Test
    void intercept_shouldNotAddUidHeader_whenMdcIsEmpty() throws IOException {
        var request = new MockClientHttpRequest();
        var execution = mock(ClientHttpRequestExecution.class);

        testObject.intercept(request, new byte[0], execution);

        assertNull(request.getHeaders().getFirst(HEADER_NAME));
        verify(requestUidProperties).mdcKey();
        verify(execution).execute(any(), any());
    }
}
