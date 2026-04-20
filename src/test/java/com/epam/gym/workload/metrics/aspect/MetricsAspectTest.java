package com.epam.gym.workload.metrics.aspect;

import com.epam.gym.workload.metrics.annotation.Measured;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

@ExtendWith(MockitoExtension.class)
class MetricsAspectTest {

    private static final String ENDPOINT_NAME = "POST_api_v1_auth_login";
    private static final String STATUS_SUCCESS = "success";
    private static final String STATUS_FAILURE = "failure";
    private static final String TYPE_COUNTER = "counter";
    private static final String TAG_ENDPOINT = "endpoint";
    private static final String TAG_STATUS = "status";
    private static final String TAG_TYPE = "type";
    private static final String TAG_EXCEPTION = "exception";
    private static final String EXCEPTION_NONE = "none";

    @Mock
    private MeterRegistry meterRegistry;
    @Mock
    private ProceedingJoinPoint joinPoint;
    @Mock
    private MethodSignature signature;
    @Mock
    private Measured measured;
    @Mock
    private Counter successCounter;
    @Mock
    private Counter failureCounter;
    @Mock
    private Timer.Builder timerBuilder;
    @Mock
    private Timer.Sample timerSample;
    @Mock
    private Timer timer;

    @InjectMocks
    private MetricsAspect testObject;

    @AfterEach
    void tearDown() {
        verifyNoMoreInteractions(meterRegistry, joinPoint, measured);
    }

    @Test
    void measure_shouldIncrementSuccessCounter_whenMethodSucceeds() throws Throwable {
        testObject.setEmbeddedValueResolver(value -> value);
        doReturn(ENDPOINT_NAME).when(measured).value();
        doReturn("result").when(joinPoint).proceed();
        doReturn(successCounter).when(meterRegistry).counter(
            anyString(),
            eq(TAG_ENDPOINT), eq(ENDPOINT_NAME),
            eq(TAG_STATUS), eq(STATUS_SUCCESS),
            eq(TAG_TYPE), eq(TYPE_COUNTER),
            eq(TAG_EXCEPTION), eq(EXCEPTION_NONE)
        );

        try (MockedStatic<Timer> timerMock = mockStatic(Timer.class)) {
            timerMock.when(() -> Timer.start(meterRegistry)).thenReturn(timerSample);
            timerMock.when(() -> Timer.builder(anyString())).thenReturn(timerBuilder);
            doReturn(timerBuilder).when(timerBuilder).tag(anyString(), anyString());
            doReturn(timerBuilder).when(timerBuilder).publishPercentileHistogram(true);
            doReturn(timerBuilder).when(timerBuilder).minimumExpectedValue(any());
            doReturn(timerBuilder).when(timerBuilder).maximumExpectedValue(any());
            doReturn(timer).when(timerBuilder).register(meterRegistry);

            testObject.measure(joinPoint, measured);
        }

        verify(successCounter).increment();
        verify(timerSample).stop(timer);
    }

    @Test
    void measure_shouldIncrementFailureCounter_whenMethodThrows() throws Throwable {
        var exception = new IllegalArgumentException("bad input");
        testObject.setEmbeddedValueResolver(value -> value);
        doReturn(ENDPOINT_NAME).when(measured).value();
        doThrow(exception).when(joinPoint).proceed();
        doReturn(failureCounter).when(meterRegistry).counter(
            anyString(),
            eq(TAG_ENDPOINT), eq(ENDPOINT_NAME),
            eq(TAG_STATUS), eq(STATUS_FAILURE),
            eq(TAG_TYPE), eq(TYPE_COUNTER),
            eq(TAG_EXCEPTION), eq("IllegalArgumentException")
        );

        try (MockedStatic<Timer> timerMock = mockStatic(Timer.class)) {
            timerMock.when(() -> Timer.start(meterRegistry)).thenReturn(timerSample);
            timerMock.when(() -> Timer.builder(anyString())).thenReturn(timerBuilder);
            doReturn(timerBuilder).when(timerBuilder).tag(anyString(), anyString());
            doReturn(timerBuilder).when(timerBuilder).publishPercentileHistogram(true);
            doReturn(timerBuilder).when(timerBuilder).minimumExpectedValue(any());
            doReturn(timerBuilder).when(timerBuilder).maximumExpectedValue(any());
            doReturn(timer).when(timerBuilder).register(meterRegistry);

            assertThrows(IllegalArgumentException.class, () -> testObject.measure(joinPoint, measured));
        }

        verify(failureCounter).increment();
        verify(timerSample).stop(timer);
    }

    @Test
    void measure_shouldRethrowOriginalException_whenExceptionOccurs() throws Throwable {
        var original = new IllegalStateException("original message");
        testObject.setEmbeddedValueResolver(value -> value);
        doReturn(ENDPOINT_NAME).when(measured).value();
        doThrow(original).when(joinPoint).proceed();
        doReturn(failureCounter).when(meterRegistry).counter(
            anyString(),
            eq(TAG_ENDPOINT), eq(ENDPOINT_NAME),
            eq(TAG_STATUS), eq(STATUS_FAILURE),
            eq(TAG_TYPE), eq(TYPE_COUNTER),
            eq(TAG_EXCEPTION), eq("IllegalStateException")
        );

        try (MockedStatic<Timer> timerMock = mockStatic(Timer.class)) {
            timerMock.when(() -> Timer.start(meterRegistry)).thenReturn(timerSample);
            timerMock.when(() -> Timer.builder(anyString())).thenReturn(timerBuilder);
            doReturn(timerBuilder).when(timerBuilder).tag(anyString(), anyString());
            doReturn(timerBuilder).when(timerBuilder).publishPercentileHistogram(true);
            doReturn(timerBuilder).when(timerBuilder).minimumExpectedValue(any());
            doReturn(timerBuilder).when(timerBuilder).maximumExpectedValue(any());
            doReturn(timer).when(timerBuilder).register(meterRegistry);

            var thrown = assertThrows(IllegalStateException.class, () -> testObject.measure(joinPoint, measured));
            assertEquals("original message", thrown.getMessage());
        }
    }

    @Test
    void measure_shouldUseFallbackName_whenAnnotationValueIsEmpty() throws Throwable {
        testObject.setEmbeddedValueResolver(value -> value);
        doReturn("").when(measured).value();
        doReturn(signature).when(joinPoint).getSignature();
        doReturn(TestService.class).when(signature).getDeclaringType();
        doReturn("doSomething").when(signature).getName();
        doReturn("result").when(joinPoint).proceed();
        doReturn(successCounter).when(meterRegistry).counter(
            anyString(),
            eq(TAG_ENDPOINT), eq("TestService.doSomething"),
            eq(TAG_STATUS), eq(STATUS_SUCCESS),
            eq(TAG_TYPE), eq(TYPE_COUNTER),
            eq(TAG_EXCEPTION), eq(EXCEPTION_NONE)
        );

        try (MockedStatic<Timer> timerMock = mockStatic(Timer.class)) {
            timerMock.when(() -> Timer.start(meterRegistry)).thenReturn(timerSample);
            timerMock.when(() -> Timer.builder(anyString())).thenReturn(timerBuilder);
            doReturn(timerBuilder).when(timerBuilder).tag(anyString(), anyString());
            doReturn(timerBuilder).when(timerBuilder).publishPercentileHistogram(true);
            doReturn(timerBuilder).when(timerBuilder).minimumExpectedValue(any());
            doReturn(timerBuilder).when(timerBuilder).maximumExpectedValue(any());
            doReturn(timer).when(timerBuilder).register(meterRegistry);

            testObject.measure(joinPoint, measured);
        }

        verify(successCounter).increment();
    }

    private static Stream<Arguments> provideExceptionTypes() {
        return Stream.of(
            Arguments.of(new IllegalArgumentException(), "IllegalArgumentException"),
            Arguments.of(new RuntimeException(), "RuntimeException"),
            Arguments.of(new NullPointerException(), "NullPointerException")
        );
    }

    @ParameterizedTest
    @MethodSource("provideExceptionTypes")
    void measure_shouldTagCorrectExceptionName_whenDifferentExceptionTypesThrown(
        Exception exception,
        String expectedTag
    ) throws Throwable {
        testObject.setEmbeddedValueResolver(value -> value);
        doReturn(ENDPOINT_NAME).when(measured).value();
        doThrow(exception).when(joinPoint).proceed();
        doReturn(failureCounter).when(meterRegistry).counter(
            anyString(),
            eq(TAG_ENDPOINT), eq(ENDPOINT_NAME),
            eq(TAG_STATUS), eq(STATUS_FAILURE),
            eq(TAG_TYPE), eq(TYPE_COUNTER),
            eq(TAG_EXCEPTION), eq(expectedTag)
        );

        try (MockedStatic<Timer> timerMock = mockStatic(Timer.class)) {
            timerMock.when(() -> Timer.start(meterRegistry)).thenReturn(timerSample);
            timerMock.when(() -> Timer.builder(anyString())).thenReturn(timerBuilder);
            doReturn(timerBuilder).when(timerBuilder).tag(anyString(), anyString());
            doReturn(timerBuilder).when(timerBuilder).publishPercentileHistogram(true);
            doReturn(timerBuilder).when(timerBuilder).minimumExpectedValue(any());
            doReturn(timerBuilder).when(timerBuilder).maximumExpectedValue(any());
            doReturn(timer).when(timerBuilder).register(meterRegistry);

            assertThrows(exception.getClass(), () -> testObject.measure(joinPoint, measured));
        }

        verify(failureCounter).increment();
        verify(timerSample).stop(timer);
    }

    static class TestService {

    }
}
