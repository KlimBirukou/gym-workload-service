package com.epam.gym.workload.controller.rest.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.NonNull;

import java.util.List;

@Builder
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record TrainerWorkloadResponse(

    @Schema(
        description = "Trainer username",
        examples = "Vesemir.Oldman"
    )
    @NonNull String username,

    @Schema(
        description = "List of yearly workload statistics for the trainer"
    )
    List<YearWorkloadResponse> years
) {

}
