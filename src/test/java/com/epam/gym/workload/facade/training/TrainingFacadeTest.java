package com.epam.gym.workload.facade.training;

import com.epam.gym.workload.controller.rest.dto.TrainingRequest;
import com.epam.gym.workload.domain.ActionType;
import com.epam.gym.workload.service.training.ITrainingService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

@ExtendWith(MockitoExtension.class)
class TrainingFacadeTest {

    private static final String USERNAME = "trainer.john";
    private static final LocalDate DATE = LocalDate.of(2024, 6, 15);
    private static final int DURATION = 60;

    @Mock
    private ITrainingService workloadService;

    @InjectMocks
    private TrainingFacade testObject;

    @AfterEach
    void tearDown() {
        verifyNoMoreInteractions(workloadService);
    }

    @Test
    void updateWorkload_shouldDelegateToService_whenRequestIsValid() {
        var request = buildRequest();

        doNothing().when(workloadService).updateWorkload(request);

        testObject.updateWorkload(request);

        verify(workloadService).updateWorkload(request);
    }

    @ParameterizedTest
    @NullSource
    void updateWorkload_shouldThrowNullPointerException_whenRequestIsNull(TrainingRequest request) {
        assertThrows(NullPointerException.class, () -> testObject.updateWorkload(request));
    }

    private static TrainingRequest buildRequest() {
        return new TrainingRequest(USERNAME, DATE, DURATION, ActionType.ADD);
    }
}
