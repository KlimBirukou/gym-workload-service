package com.epam.gym.workload.facade.workload;

import com.epam.gym.workload.controller.rest.dto.WorkloadResponse;
import com.epam.gym.workload.service.workload.IWorkloadService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

@ExtendWith(MockitoExtension.class)
class WorkloadFacadeTest {

    private static final String USERNAME = "username";

    @Mock
    private IWorkloadService workloadService;

    @InjectMocks
    private WorkloadFacade testObject;

    @AfterEach
    void tearDown() {
        verifyNoMoreInteractions(workloadService);
    }

    @Test
    void getWorkload_shouldReturnWorkloadResponse_whenUsernameIsValid() {
        var expected = new WorkloadResponse(USERNAME, List.of());
        doReturn(expected).when(workloadService).getWorkload(USERNAME);

        var result = testObject.getWorkload(USERNAME);

        assertSame(expected, result);
        verify(workloadService).getWorkload(USERNAME);
    }

    @ParameterizedTest
    @NullSource
    void getWorkload_shouldThrowNullPointerException_whenUsernameIsNull(String username) {
        assertThrows(NullPointerException.class, () -> testObject.getWorkload(username));
    }
}
