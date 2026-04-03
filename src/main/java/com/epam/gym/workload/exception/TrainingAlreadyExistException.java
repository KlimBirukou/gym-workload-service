package com.epam.gym.workload.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;

@Getter
@RequiredArgsConstructor
public class TrainingAlreadyExistException extends RuntimeException {

    private final String username;
    private final LocalDate date;
}
