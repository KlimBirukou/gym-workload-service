package com.epam.gym.workload.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;

@Getter
@RequiredArgsConstructor
public class TrainingNotFoundException extends RuntimeException {

    private final String username;
    private final LocalDate date;

}
