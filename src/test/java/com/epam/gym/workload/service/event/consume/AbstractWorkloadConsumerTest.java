package com.epam.gym.workload.service.event.consume;

import com.epam.gym.workload.domain.update.WorkloadUpdateEvent;
import com.epam.gym.workload.domain.update.WorkloadUpdateEventType;
import com.epam.gym.workload.service.event.consumer.AbstractWorkloadEventConsumer;
import lombok.NonNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class AbstractWorkloadConsumerTest {

    private static final String USERNAME = "username";
    private static final LocalDate DATE = LocalDate.of(2024, 6, 15);
    private static final int DURATION = 60;

    private StubWorkloadConsumer testObject;

    @BeforeEach
    void setUp() {
        testObject = new StubWorkloadConsumer();
    }

    @Test
    void consume_shouldDelegateToDoConsume_whenEventIsValid() {
        testObject.consume(getEvent());

        assertTrue(testObject.wasCalled());
    }

    @ParameterizedTest
    @NullSource
    void consume_shouldThrowException_whenArgumentIsNull(WorkloadUpdateEvent event) {
        assertThrows(NullPointerException.class, () -> testObject.consume(event));
        assertFalse(testObject.wasCalled());
    }

    private static WorkloadUpdateEvent getEvent() {
        return WorkloadUpdateEvent.builder()
            .trainerUsername(USERNAME)
            .trainingDate(DATE)
            .trainingDuration(DURATION)
            .eventType(WorkloadUpdateEventType.ADD)
            .build();
    }

    private static class StubWorkloadConsumer extends AbstractWorkloadEventConsumer {

        private boolean called = false;

        @Override
        public WorkloadUpdateEventType getApplicableType() {
            return WorkloadUpdateEventType.ADD;
        }

        @Override
        protected void doConsume(@NonNull WorkloadUpdateEvent event) {
            called = true;
        }

        boolean wasCalled() {
            return called;
        }
    }
}
