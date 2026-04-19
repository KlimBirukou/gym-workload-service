package com.epam.gym.workload.listener;

import com.epam.gym.workload.domain.ActionType;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import java.time.LocalDate;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record WorkloadUpdateEvent(

    String trainerUsername,
    LocalDate trainingDate,
    int trainingDuration,
    ActionType actionType
) {

}
