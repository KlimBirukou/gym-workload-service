package com.epam.gym.workload.facade.event;

import com.epam.gym.workload.domain.update.WorkloadUpdateEvent;
import com.epam.gym.workload.domain.update.WorkloadUpdateEventType;
import com.epam.gym.workload.service.event.IEventService;
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
    private IEventService workloadService;

    @InjectMocks
    private EventFacade testObject;

    @AfterEach
    void tearDown() {
        verifyNoMoreInteractions(workloadService);
    }

    @Test
    void updateWorkload_shouldDelegateToService_whenRequestIsValid() {
        var request = getEvent();

        doNothing().when(workloadService).updateWorkload(request);

        testObject.updateWorkload(request);

        verify(workloadService).updateWorkload(request);
    }

    @ParameterizedTest
    @NullSource
    void updateWorkload_shouldThrowNullPointerException_whenRequestIsNull(WorkloadUpdateEvent request) {
        assertThrows(NullPointerException.class, () -> testObject.updateWorkload(request));
    }

    private static WorkloadUpdateEvent getEvent() {
        return WorkloadUpdateEvent.builder()
            .trainerUsername(USERNAME)
            .trainingDate(DATE)
            .trainingDuration(DURATION)
            .eventType(WorkloadUpdateEventType.ADD)
            .build();
    }
}
