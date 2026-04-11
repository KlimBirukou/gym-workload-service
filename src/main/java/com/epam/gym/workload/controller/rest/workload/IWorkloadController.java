package com.epam.gym.workload.controller.rest.workload;

import com.epam.gym.workload.controller.rest.dto.WorkloadResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Tag(
    name = "Statistic",
    description = "Workload statistic about the trainer for chosen period"
)
@RequestMapping("/api/v1/workload")
public interface IWorkloadController {

    @Operation(
        summary = "Get trainer statistic",
        description = "Retrieve an object contained trainer workload statistic measured by years and month," +
            " counted in worked hours."
    )
    @ApiResponse(
        responseCode = "204",
        description = "Trainer statistic successfully retrieved",
        content = @Content(
            mediaType = MediaType.APPLICATION_JSON_VALUE,
            array = @ArraySchema(
                schema = @Schema(implementation = Object.class)
            ))
    )
    @ApiResponse(
        responseCode = "404",
        description = "Trainer not found",
        content = @Content(
            mediaType = MediaType.APPLICATION_JSON_VALUE,
            examples = @ExampleObject(
                name = "Trainer not found",
                value = """
                    {
                      "error": "NOT_FOUND",
                      "description": "Trainer [Night.King] was not found"
                    }
                    """
            )
        )
    )
    @GetMapping
    WorkloadResponse getStatistic(@RequestParam String username);
}
