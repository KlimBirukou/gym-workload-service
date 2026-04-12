package com.epam.gym.workload.repository;

import com.epam.gym.workload.domain.Training;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.mapstruct.factory.Mappers;

import java.time.LocalDate;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

class TrainingEntityToTrainingMapperTest {

    private static final UUID UID = UUID.randomUUID();
    private static final String USERNAME = "username";
    private static final int DURATION = 60;
    private static final LocalDate DATE = LocalDate.of(2024, 6, 15);

    private static final TrainingEntityToTrainingMapper testObject =
        Mappers.getMapper(TrainingEntityToTrainingMapper.class);

    @Test
    void convert_shouldConvertEntityToTraining_whenEntityIsValid() {
        var entity = buildEntity();

        var result = testObject.convert(entity);

        assertNotNull(result);
        assertEquals(USERNAME, result.getUsername());
        assertEquals(DURATION, result.getDuration());
        assertEquals(DATE, result.getDate());
    }

    @ParameterizedTest
    @NullSource
    void convert_shouldThrowException_whenEntityIsNull(TrainingEntity entity) {
        var result = testObject.convert(entity);

        assertNull(result);
    }

    @Test
    void convert_shouldConvertTrainingToEntity_whenTrainingIsValid() {
        var training = buildTraining();

        var result = testObject.convert(training);

        assertNotNull(result);
        assertEquals(USERNAME, result.getUsername());
        assertEquals(DURATION, result.getDuration());
        assertEquals(DATE, result.getDate());
    }

    @ParameterizedTest
    @NullSource
    void convert_shouldThrowException_whenTrainingIsNull(Training training) {
        var result = testObject.convert(training);

        assertNull(result);
    }

    private static TrainingEntity buildEntity() {
        return TrainingEntity.builder()
            .uid(UID)
            .username(USERNAME)
            .duration(DURATION)
            .date(DATE)
            .build();
    }

    private static Training buildTraining() {
        return Training.builder()
            .username(USERNAME)
            .duration(DURATION)
            .date(DATE)
            .build();
    }

}
