package com.epam.gym.workload.controller.rest.training;

import com.epam.gym.workload.controller.rest.dto.TrainingRequest;
import com.epam.gym.workload.domain.ActionType;
import com.epam.gym.workload.facade.training.ITrainingFacade;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

@ExtendWith(MockitoExtension.class)
class TrainingControllerTest {

    private static final String USERNAME = "username";
    private static final LocalDate DATE = LocalDate.of(2024, 6, 15);
    private static final int DURATION = 60;

    @Mock
    private ITrainingFacade workloadFacade;

    @InjectMocks
    private TrainingController testObject;

    @AfterEach
    void tearDown() {
        verifyNoMoreInteractions(workloadFacade);
    }

    @Test
    void updateWorkload() {
        var request = buildRequest();
        doNothing().when(workloadFacade).updateWorkload(request);

        testObject.updateWorkload(request);

        verify(workloadFacade).updateWorkload(request);
    }

    private static TrainingRequest buildRequest() {
        return new TrainingRequest(USERNAME, DATE, DURATION, ActionType.ADD);
    }
}
