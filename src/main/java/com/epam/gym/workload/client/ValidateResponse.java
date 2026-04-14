package com.epam.gym.workload.client;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Builder;

@Builder
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record ValidateResponse(
    boolean valid,
    String username
) {
    public static ValidateResponse valid(String username) {
        return new ValidateResponse(true, username);
    }

    public static ValidateResponse invalid() {
        return new ValidateResponse(false, null);
    }
}
