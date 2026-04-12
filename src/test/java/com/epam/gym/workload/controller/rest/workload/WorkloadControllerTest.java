package com.epam.gym.workload.controller.rest.workload;

import com.epam.gym.workload.controller.rest.dto.WorkloadResponse;
import com.epam.gym.workload.facade.workload.IWorkloadFacade;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

@ExtendWith(MockitoExtension.class)
class WorkloadControllerTest {

    private static final String USERNAME = "username";

    @Mock
    private IWorkloadFacade workloadFacade;

    @InjectMocks
    private WorkloadController testObject;

    @AfterEach
    void tearDown() {
        verifyNoMoreInteractions(workloadFacade);
    }

    @Test
    void getStatistic_shouldReturnWorkloadResponse_whenUsernameIsValid() {
        var expected = new WorkloadResponse(USERNAME, List.of());
        doReturn(expected).when(workloadFacade).getWorkload(USERNAME);

        var actual = testObject.getStatistic(USERNAME);

        assertNotNull(actual);
        assertEquals(expected, actual);
        verify(workloadFacade).getWorkload(USERNAME);
    }
}
