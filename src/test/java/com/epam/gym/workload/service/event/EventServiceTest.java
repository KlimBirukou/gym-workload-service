package com.epam.gym.workload.service.event;

import com.epam.gym.workload.domain.update.WorkloadUpdateEvent;
import com.epam.gym.workload.domain.update.WorkloadUpdateEventType;
import com.epam.gym.workload.service.event.consumer.IWorkloadUpdateEventConsumer;
import com.epam.gym.workload.service.event.consumer.registry.IWorkloadUpdateEventConsumerRegistry;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class EventServiceTest {

    private static final String USERNAME = "username";
    private static final LocalDate DATE = LocalDate.of(2024, 6, 15);
    private static final int DURATION = 60;
    public static final WorkloadUpdateEventType WORKLOAD_UPDATE_EVENT_TYPE = WorkloadUpdateEventType.ADD;

    @Mock
    private IWorkloadUpdateEventConsumerRegistry registry;
    @Mock
    private IWorkloadUpdateEventConsumer consumer;

    @InjectMocks
    private EventService testObject;

    @Test
    void updateWorkload_shouldDelegateToConsumer_whenConsumerFound() {
        var event = getEvent();
        doReturn(Optional.of(consumer)).when(registry).get(WORKLOAD_UPDATE_EVENT_TYPE);

        testObject.updateWorkload(event);

        verify(registry).get(WORKLOAD_UPDATE_EVENT_TYPE);
        verify(consumer).consume(event);
    }

    @Test
    void updateWorkload_shouldThrowUnsupportedOperationException_whenNoConsumerFound() {
        var event = getEvent();
        doReturn(Optional.empty()).when(registry).get(WORKLOAD_UPDATE_EVENT_TYPE);

        assertThrows(UnsupportedOperationException.class, () -> testObject.updateWorkload(event));

        verify(registry).get(WORKLOAD_UPDATE_EVENT_TYPE);
    }

    @ParameterizedTest
    @NullSource
    void updateWorkload_shouldThrowException_whenArgumentIsNull(WorkloadUpdateEvent event) {
        assertThrows(NullPointerException.class, () -> testObject.updateWorkload(event));
    }

    private static WorkloadUpdateEvent getEvent() {
        return WorkloadUpdateEvent.builder()
            .trainerUsername(USERNAME)
            .trainingDate(DATE)
            .trainingDuration(DURATION)
            .eventType(WORKLOAD_UPDATE_EVENT_TYPE)
            .build();
    }
}
