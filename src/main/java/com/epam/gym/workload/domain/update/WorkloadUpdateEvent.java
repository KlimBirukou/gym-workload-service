package com.epam.gym.workload.domain.update;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Builder;

import java.time.LocalDate;

@Builder
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record WorkloadUpdateEvent(

    String trainerUsername,
    LocalDate trainingDate,
    int trainingDuration,
    WorkloadUpdateEventType eventType
) {

}
