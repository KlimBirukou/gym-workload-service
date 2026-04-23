package com.epam.gym.workload.service.event.consume.regestry;

import com.epam.gym.workload.domain.update.WorkloadUpdateEventType;
import com.epam.gym.workload.service.event.consumer.IWorkloadUpdateEventConsumer;
import com.epam.gym.workload.service.event.consumer.registry.WorkloadUpdateEventConsumerRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.doReturn;

@ExtendWith(MockitoExtension.class)
class WorkloadUpdateEventConsumerRegistryTest {

    @Mock
    private IWorkloadUpdateEventConsumer addConsumer;

    @Mock
    private IWorkloadUpdateEventConsumer deleteConsumer;

    private WorkloadUpdateEventConsumerRegistry testObject;

    @BeforeEach
    void setUp() {
        doReturn(WorkloadUpdateEventType.ADD).when(addConsumer).getApplicableType();
        doReturn(WorkloadUpdateEventType.DELETE).when(deleteConsumer).getApplicableType();
        testObject = new WorkloadUpdateEventConsumerRegistry(List.of(addConsumer, deleteConsumer));
        testObject.setUp();
    }

    @Test
    void get_shouldReturnAddConsumer_whenEventTypeIsAdd() {
        var result = testObject.get(WorkloadUpdateEventType.ADD);

        assertTrue(result.isPresent());
        assertSame(addConsumer, result.get());
    }

    @Test
    void get_shouldReturnDeleteConsumer_whenEventTypeIsDelete() {
        var result = testObject.get(WorkloadUpdateEventType.DELETE);

        assertTrue(result.isPresent());
        assertSame(deleteConsumer, result.get());
    }

    @Test
    void get_shouldReturnNonEmptyOptional_forRegisteredTypes() {
        assertTrue(testObject.get(WorkloadUpdateEventType.ADD).isPresent());
        assertTrue(testObject.get(WorkloadUpdateEventType.DELETE).isPresent());
    }

    @ParameterizedTest
    @NullSource
    void get_shouldReturnEmptyOptional_whenEventTypeIsNull(WorkloadUpdateEventType eventType) {
        assertTrue(testObject.get(eventType).isEmpty());
    }
}
