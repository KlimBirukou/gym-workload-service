package com.epam.gym.workload.controller.rest.dto;

import com.epam.gym.workload.domain.ActionType;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.time.LocalDate;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record TrainingRequest(

    @NotBlank(message = "Trainer username is required")
    String trainerUsername,
    @NotNull(message = "Training date is required")
    LocalDate trainingDate,
    @Positive(message = "Training duration must be positive")
    int trainingDuration,
    @NotNull(message = "Action type is required")
    ActionType actionType
) {

}
