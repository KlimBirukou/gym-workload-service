package com.epam.gym.workload.service.workload;

import com.epam.gym.workload.domain.Training;
import com.epam.gym.workload.exception.TrainerNotFoundException;
import com.epam.gym.workload.repository.IWorkloadRepository;
import com.epam.gym.workload.repository.TrainingEntity;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.convert.ConversionService;

import java.time.LocalDate;
import java.time.Month;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

@ExtendWith(MockitoExtension.class)
class WorkloadServiceTest {

    private static final String USERNAME = "trainer_username";
    private static final LocalDate DATE_2025_DEC = LocalDate.of(2025, 12, 5);
    private static final LocalDate DATE_2026_JAN = LocalDate.of(2026, 1, 15);
    private static final LocalDate DATE_2026_JAN_2 = LocalDate.of(2026, 1, 28);
    private static final LocalDate DATE_2026_FEB = LocalDate.of(2026, 2, 20);
    private static final int DURATION_60 = 60;
    private static final int DURATION_90 = 90;

    @Mock
    private IWorkloadRepository workloadRepository;
    @Mock
    private ConversionService conversionService;

    @InjectMocks
    private WorkloadService testObject;

    @AfterEach
    void tearDown() {
        verifyNoMoreInteractions(workloadRepository, conversionService);
    }

    @Test
    void getWorkload_shouldReturnSingleYearSingleMonth_whenOneTrainingExists() {
        var entity = new TrainingEntity();
        var training = buildTraining(DATE_2026_JAN, DURATION_60);
        doReturn(List.of(entity)).when(workloadRepository).findAllByUsername(USERNAME);
        doReturn(training).when(conversionService).convert(entity, Training.class);

        var result = testObject.getWorkload(USERNAME);

        assertNotNull(result);
        assertEquals(USERNAME, result.username());
        assertEquals(1, result.years().size());
        assertEquals(2026, result.years().getFirst().year());
        assertEquals(1, result.years().getFirst().months().size());
        assertEquals(Month.JANUARY, result.years().getFirst().months().getFirst().month());
        assertEquals(DURATION_60, result.years().getFirst().months().getFirst().totalDuration());
        verify(workloadRepository).findAllByUsername(USERNAME);
        verify(conversionService).convert(any(TrainingEntity.class), eq(Training.class));
    }

    @Test
    void getWorkload_shouldReturnTwoMonthsSorted_whenTrainingsInDifferentMonthsSameYear() {
        var entity1 = new TrainingEntity();
        var entity2 = new TrainingEntity();
        var training1 = buildTraining(DATE_2026_JAN, DURATION_60);
        var training2 = buildTraining(DATE_2026_FEB, DURATION_90);
        doReturn(List.of(entity1, entity2)).when(workloadRepository).findAllByUsername(USERNAME);
        doReturn(training1).when(conversionService).convert(entity1, Training.class);
        doReturn(training2).when(conversionService).convert(entity2, Training.class);

        var result = testObject.getWorkload(USERNAME);

        assertNotNull(result);
        assertEquals(1, result.years().size());
        assertEquals(2026, result.years().getFirst().year());
        assertEquals(2, result.years().getFirst().months().size());
        assertEquals(Month.JANUARY, result.years().getFirst().months().get(0).month());
        assertEquals(DURATION_60, result.years().getFirst().months().get(0).totalDuration());
        assertEquals(Month.FEBRUARY, result.years().getFirst().months().get(1).month());
        assertEquals(DURATION_90, result.years().getFirst().months().get(1).totalDuration());
        verify(workloadRepository).findAllByUsername(USERNAME);
        verify(conversionService, times(2)).convert(any(TrainingEntity.class), eq(Training.class));
    }

    @Test
    void getWorkload_shouldSumDurations_whenMultipleTrainingsInSameMonth() {
        var entity1 = new TrainingEntity();
        var entity2 = new TrainingEntity();
        var training1 = buildTraining(DATE_2026_JAN, DURATION_60);
        var training2 = buildTraining(DATE_2026_JAN_2, DURATION_90);
        doReturn(List.of(entity1, entity2)).when(workloadRepository).findAllByUsername(USERNAME);
        doReturn(training1).when(conversionService).convert(entity1, Training.class);
        doReturn(training2).when(conversionService).convert(entity2, Training.class);

        var result = testObject.getWorkload(USERNAME);

        assertNotNull(result);
        assertEquals(1, result.years().size());
        assertEquals(1, result.years().getFirst().months().size());
        assertEquals(Month.JANUARY, result.years().getFirst().months().getFirst().month());
        assertEquals(DURATION_60 + DURATION_90, result.years().getFirst().months().getFirst().totalDuration());
        verify(workloadRepository).findAllByUsername(USERNAME);
        verify(conversionService, times(2)).convert(any(TrainingEntity.class), eq(Training.class));
    }

    @Test
    void getWorkload_shouldReturnTwoYearsSorted_whenTrainingsSpanMultipleYears() {
        var entity1 = new TrainingEntity();
        var entity2 = new TrainingEntity();
        var training1 = buildTraining(DATE_2025_DEC, DURATION_60);
        var training2 = buildTraining(DATE_2026_JAN, DURATION_90);
        doReturn(List.of(entity1, entity2)).when(workloadRepository).findAllByUsername(USERNAME);
        doReturn(training1).when(conversionService).convert(entity1, Training.class);
        doReturn(training2).when(conversionService).convert(entity2, Training.class);

        var result = testObject.getWorkload(USERNAME);

        assertNotNull(result);
        assertEquals(2, result.years().size());
        assertEquals(2025, result.years().getFirst().year());
        assertEquals(Month.DECEMBER, result.years().get(0).months().getFirst().month());
        assertEquals(DURATION_60, result.years().get(0).months().getFirst().totalDuration());
        assertEquals(2026, result.years().get(1).year());
        assertEquals(Month.JANUARY, result.years().get(1).months().getFirst().month());
        assertEquals(DURATION_90, result.years().get(1).months().getFirst().totalDuration());
        verify(workloadRepository).findAllByUsername(USERNAME);
        verify(conversionService, times(2)).convert(any(TrainingEntity.class), eq(Training.class));
    }

    @Test
    void getWorkload_shouldThrowException_whenNoTrainingsFound() {
        doReturn(List.of()).when(workloadRepository).findAllByUsername(USERNAME);

        assertThrows(TrainerNotFoundException.class, () -> testObject.getWorkload(USERNAME));

        verify(workloadRepository).findAllByUsername(USERNAME);
    }

    @ParameterizedTest
    @NullSource
    void getWorkload_shouldThrowException_whenUsernameNull(String username) {
        assertThrows(NullPointerException.class, () -> testObject.getWorkload(username));
    }

    private static Training buildTraining(LocalDate date, int duration) {
        return Training.builder()
            .username(USERNAME)
            .date(date)
            .duration(duration)
            .build();
    }
}
