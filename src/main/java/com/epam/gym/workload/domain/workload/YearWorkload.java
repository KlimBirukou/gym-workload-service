package com.epam.gym.workload.domain.workload;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.util.List;

@Builder
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record YearWorkload(

    @Schema(
        description = "Calendar year",
        example = "2026"
    )
    int year,

    @Schema(
        description = "List of monthly workload statistics for the given year"
    )
    List<MonthWorkload> months
) {}
