package com.epam.gym.workload.domain.workload;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record MonthWorkload(

    @Schema(
        description = "Calendar month",
        example = "6"
    )
    int month,

    @Schema(
        description = "Total training duration for the month, measured in minutes",
        example = "120"
    )
    int trainingSummaryDuration
) {

}
