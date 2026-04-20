package com.epam.gym.workload.metrics.endpoint;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;
import org.springframework.boot.actuate.endpoint.annotation.Selector;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Component
@Endpoint(id = "custom")
@RequiredArgsConstructor
public class CustomMetricsEndpoint {

    private static final String TAG_TYPE = "type";
    private static final String TAG_STATUS = "status";
    private static final String TAG_EXCEPTION = "exception";
    private static final String STATUS_SUCCESS = "success";
    private static final String STATUS_FAILURE = "failure";
    private static final String TYPE_COUNTER = "counter";
    private static final String TYPE_TIMER = "timer";
    public static final String UNKNOWN = "Unknown";
    private static final String TAG_ENDPOINT = "endpoint";
    public static final String GYM_API_REQUESTS = "gym.api.requests";

    private final MeterRegistry meterRegistry;

    @ReadOperation
    public CustomMetricsResponse getMetrics(@Selector String metricName) {
        long success = getCounterValue(metricName);
        long failures = getFailuresTotal(metricName);
        long totalAttempts = success + failures;
        return CustomMetricsResponse.builder()
            .metric(metricName)
            .attempts(totalAttempts)
            .success(success)
            .failures(failures)
            .failuresByType(getFailuresMap(metricName))
            .successRate(calculateSuccessRate(success, totalAttempts))
            .timing(getTimingMetrics(metricName))
            .build();
    }

    private long getCounterValue(@NonNull String endpointName) {
        return (long) meterRegistry.find(GYM_API_REQUESTS)
            .tag(TAG_ENDPOINT, endpointName)
            .tag(TAG_TYPE, TYPE_COUNTER)
            .tag(TAG_STATUS, CustomMetricsEndpoint.STATUS_SUCCESS)
            .counters().stream().mapToDouble(Counter::count).sum();
    }

    private long getFailuresTotal(@NonNull String endpointName) {
        return (long) meterRegistry.find(GYM_API_REQUESTS)
            .tag(TAG_ENDPOINT, endpointName)
            .tag(TAG_TYPE, TYPE_COUNTER)
            .tag(TAG_STATUS, STATUS_FAILURE)
            .counters().stream().mapToDouble(Counter::count).sum();
    }

    private Map<String, Long> getFailuresMap(@NonNull String endpointName) {
        return meterRegistry.find(GYM_API_REQUESTS)
            .tag(TAG_ENDPOINT, endpointName)
            .tag(TAG_TYPE, TYPE_COUNTER)
            .tag(TAG_STATUS, STATUS_FAILURE)
            .counters().stream()
            .collect(Collectors.toMap(
                this::getExceptionName,
                counter -> (long) counter.count(),
                Long::sum
            ));
    }

    private Map<String, Double> getTimingMetrics(@NonNull String endpointName) {
        Map<String, Double> timing = new HashMap<>();
        Timer timer = meterRegistry.find(GYM_API_REQUESTS)
            .tag(TAG_ENDPOINT, endpointName)
            .tag(TAG_TYPE, TYPE_TIMER)
            .timer();
        if (Objects.nonNull(timer)) {
            var totalMs = timer.totalTime(TimeUnit.MILLISECONDS);
            var count = timer.count();
            timing.put("count", (double) count);
            timing.put("avg_ms", totalMs / count);
            timing.put("max_ms", timer.max(TimeUnit.MILLISECONDS));
            timing.put("total_ms", totalMs);
        }
        return timing;
    }

    private String getExceptionName(@NonNull Counter counter) {
        String value = counter.getId().getTag(TAG_EXCEPTION);
        return Objects.isNull(value) ? UNKNOWN : value;
    }

    private String calculateSuccessRate(long success, long total) {
        return total <= 0
            ? "0.00%"
            : String.format("%.2f%%", (success * 100.0) / total);
    }
}
