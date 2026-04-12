package com.epam.gym.workload.service.training;

import com.epam.gym.workload.controller.rest.dto.TrainingRequest;
import com.epam.gym.workload.domain.ActionType;
import com.epam.gym.workload.domain.Training;
import com.epam.gym.workload.exception.TrainingAlreadyExistException;
import com.epam.gym.workload.exception.TrainingNotFoundException;
import com.epam.gym.workload.repository.IWorkloadRepository;
import com.epam.gym.workload.repository.TrainingEntity;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.convert.ConversionService;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TrainingServiceTest {

    private static final String USERNAME = "username";
    private static final LocalDate DATE = LocalDate.of(2024, 6, 15);
    private static final int DURATION = 60;

    @Mock
    private IWorkloadRepository workloadRepository;
    @Mock
    private ConversionService conversionService;

    @InjectMocks
    private TrainingService testObject;

    @BeforeEach
    void setUp() throws Exception {
        var initHandlers = TrainingService.class.getDeclaredMethod("initHandlers");
        initHandlers.setAccessible(true);
        initHandlers.invoke(testObject);
    }

    @AfterEach
    void tearDown() {
        verifyNoMoreInteractions(workloadRepository, conversionService);
    }

    @Test
    void updateWorkload_shouldSaveTraining_whenActionTypeIsAdd() {
        var request = buildRequest(ActionType.ADD);
        var entity = new TrainingEntity();

        doReturn(false).when(workloadRepository).existsByUsernameAndDate(USERNAME, DATE);
        doReturn(entity).when(conversionService).convert(any(Training.class), eq(TrainingEntity.class));

        testObject.updateWorkload(request);

        verify(workloadRepository).existsByUsernameAndDate(USERNAME, DATE);
        verify(conversionService).convert(any(Training.class), eq(TrainingEntity.class));
        verify(workloadRepository).save(entity);
        assertNotNull(entity.getUid());
    }

    @Test
    void updateWorkload_shouldThrowTrainingAlreadyExistException_whenTrainingAlreadyExistsOnAdd() {
        var request = buildRequest(ActionType.ADD);

        doReturn(true).when(workloadRepository).existsByUsernameAndDate(USERNAME, DATE);

        assertThrows(TrainingAlreadyExistException.class, () -> testObject.updateWorkload(request));

        verify(workloadRepository).existsByUsernameAndDate(USERNAME, DATE);
    }

    @Test
    void updateWorkload_shouldDeleteTraining_whenActionTypeIsDelete() {
        var request = buildRequest(ActionType.DELETE);
        var entity = new TrainingEntity();

        doReturn(Optional.of(entity)).when(workloadRepository).findByUsernameAndDate(USERNAME, DATE);

        testObject.updateWorkload(request);

        verify(workloadRepository).findByUsernameAndDate(USERNAME, DATE);
        verify(workloadRepository).delete(entity);
    }

    @Test
    void updateWorkload_shouldThrowTrainingNotFoundException_whenEntityNotFoundOnDelete() {
        var request = buildRequest(ActionType.DELETE);

        doReturn(Optional.empty()).when(workloadRepository).findByUsernameAndDate(USERNAME, DATE);

        assertThrows(TrainingNotFoundException.class, () -> testObject.updateWorkload(request));

        verify(workloadRepository).findByUsernameAndDate(USERNAME, DATE);
    }

    @ParameterizedTest
    @NullSource
    void updateWorkload_shouldThrowNullPointerException_whenRequestIsNull(TrainingRequest request) {
        assertThrows(NullPointerException.class, () -> testObject.updateWorkload(request));
    }

    private static TrainingRequest buildRequest(ActionType actionType) {
        return new TrainingRequest(USERNAME, DATE, DURATION, actionType);
    }
}
