package com.epam.gym.workload.service.workload;

import com.epam.gym.workload.domain.workload.TrainerWorkload;
import com.epam.gym.workload.exception.TrainerNotFoundException;
import com.epam.gym.workload.repository.IWorkloadRepository;
import com.epam.gym.workload.repository.entity.TrainerDocument;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;


@ExtendWith(MockitoExtension.class)
class WorkloadServiceTest {

    private static final String USERNAME = "trainer_username";

    @Mock
    private IWorkloadRepository workloadRepository;
    @Mock
    private IWorkloadMapper workloadMapper;
    @Mock
    private TrainerWorkload trainerWorkload;

    @InjectMocks
    private WorkloadService testObject;

    @AfterEach
    void tearDown() {
        verifyNoMoreInteractions(workloadRepository, workloadMapper);
    }

    @Test
    void getWorkload_shouldReturnMappedWorkload_whenDocumentExists() {
        var document = TrainerDocument.builder().username(USERNAME).build();

        doReturn(Optional.of(document)).when(workloadRepository).findById(USERNAME);
        doReturn(trainerWorkload).when(workloadMapper).toWorkload(document);

        var result = testObject.getWorkload(USERNAME);

        assertNotNull(result);
        assertSame(trainerWorkload, result);

        verify(workloadRepository).findById(USERNAME);
        verify(workloadMapper).toWorkload(document);
    }

    @Test
    void getWorkload_shouldThrowException_whenDocumentNotFound() {
        doReturn(Optional.empty()).when(workloadRepository).findById(USERNAME);

        assertThrows(TrainerNotFoundException.class, () -> testObject.getWorkload(USERNAME));

        verify(workloadRepository).findById(USERNAME);
    }

    @ParameterizedTest
    @NullSource
    void getWorkload_shouldThrowException_whenUsernameNull(String username) {
        assertThrows(NullPointerException.class, () -> testObject.getWorkload(username));
    }
}
