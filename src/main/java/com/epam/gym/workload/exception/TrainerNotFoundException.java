package com.epam.gym.workload.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class TrainerNotFoundException extends RuntimeException {

    private final String username;
}
