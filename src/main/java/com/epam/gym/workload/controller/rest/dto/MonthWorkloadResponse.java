package com.epam.gym.workload.controller.rest.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.time.Month;

@Builder
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record MonthWorkloadResponse(

    @Schema(
        description = "Calendar month",
        example = "6"
    )
    Month month,

    @Schema(
        description = "Total training duration for the month, measured in minutes",
        example = "120"
    )
    int totalDuration
) {

}
