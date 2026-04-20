package com.epam.gym.workload.metrics.endpoint;

import lombok.Builder;

import java.util.Map;

@Builder
public record CustomMetricsResponse(
    String metric,
    long attempts,
    long success,
    long failures,
    String successRate,
    Map<String, Long> failuresByType,
    Map<String, Double> timing
) {

}
