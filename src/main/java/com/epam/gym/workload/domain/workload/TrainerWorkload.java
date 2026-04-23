package com.epam.gym.workload.domain.workload;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.NonNull;

import java.util.List;

@Builder
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record TrainerWorkload(

    @Schema(
        description = "Trainer username",
        examples = "Vesemir.Oldman"
    )
    @NonNull String username,

    @Schema(
        description = "List of yearly workload statistics for the trainer"
    )
    List<YearWorkload> years
) {

}
