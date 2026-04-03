package com.epam.gym.workload.contrtoller.advice;

import lombok.Builder;
import lombok.NonNull;

@Builder
public record ErrorDto(
    @NonNull String error,
    @NonNull String description
) {

}
