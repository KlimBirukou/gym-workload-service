package com.epam.gym.workload.metrics.aspect;

import com.epam.gym.workload.metrics.annotation.Measured;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.context.EmbeddedValueResolverAware;
import org.springframework.stereotype.Component;
import org.springframework.util.StringValueResolver;

import java.time.Duration;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class MetricsAspect implements EmbeddedValueResolverAware {

    private static final String BASE_METRIC_NAME = "gym.api.requests";
    private static final String TAG_ENDPOINT = "endpoint";

    private static final String TAG_TYPE = "type";
    private static final String TAG_STATUS = "status";
    private static final String TAG_EXCEPTION = "exception";
    private static final String STATUS_SUCCESS = "success";
    private static final String STATUS_FAILURE = "failure";
    private static final String TYPE_COUNTER = "counter";
    private static final String TYPE_TIMER = "timer";

    private final MeterRegistry meterRegistry;
    private StringValueResolver valueResolver;

    @Around("@annotation(measured)")
    public Object measure(@NonNull ProceedingJoinPoint joinPoint, @NonNull Measured measured) throws Throwable {
        String endpointName = getEndpointName(joinPoint, measured);
        Timer.Sample sample = Timer.start(meterRegistry);

        try {
            Object result = joinPoint.proceed();
            meterRegistry.counter(BASE_METRIC_NAME,
                TAG_ENDPOINT, endpointName,
                TAG_STATUS, STATUS_SUCCESS,
                TAG_TYPE, TYPE_COUNTER,
                TAG_EXCEPTION, "none"
            ).increment();
            return result;
        } catch (Exception e) {
            meterRegistry.counter(BASE_METRIC_NAME,
                TAG_ENDPOINT, endpointName,
                TAG_STATUS, STATUS_FAILURE,
                TAG_TYPE, TYPE_COUNTER,
                TAG_EXCEPTION, e.getClass().getSimpleName()
            ).increment();
            throw e;
        } finally {
            Timer timer = Timer.builder(BASE_METRIC_NAME)
                .tag(TAG_ENDPOINT, endpointName)
                .tag(TAG_TYPE, TYPE_TIMER)
                .publishPercentileHistogram(true)
                .minimumExpectedValue(Duration.ofMillis(1))
                .maximumExpectedValue(Duration.ofSeconds(30))
                .register(meterRegistry);
            sample.stop(timer);
        }
    }

    private String getEndpointName(ProceedingJoinPoint joinPoint, Measured measured) {
        String annotationValue = measured.value().isEmpty()
            ? joinPoint.getSignature().getDeclaringType().getSimpleName() + "." + joinPoint.getSignature().getName()
            : measured.value();
        return valueResolver.resolveStringValue(annotationValue);
    }

    @Override
    public void setEmbeddedValueResolver(@NonNull StringValueResolver resolver) {
        this.valueResolver = resolver;
    }
}
