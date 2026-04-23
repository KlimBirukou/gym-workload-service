package com.epam.gym.workload.service.event.consume;

import com.epam.gym.workload.domain.update.WorkloadUpdateEvent;
import com.epam.gym.workload.domain.update.WorkloadUpdateEventType;
import com.epam.gym.workload.exception.TrainingNotFoundException;
import com.epam.gym.workload.repository.IWorkloadRepository;
import com.epam.gym.workload.repository.entity.MonthDocument;
import com.epam.gym.workload.repository.entity.TrainerDocument;
import com.epam.gym.workload.repository.entity.YearDocument;
import com.epam.gym.workload.service.event.consumer.DeleteWorkloadEventConsumer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

@ExtendWith(MockitoExtension.class)
class DeleteWorkloadEventConsumerTest {

    private static final String USERNAME = "username";
    private static final LocalDate DATE = LocalDate.of(2026, 6, 6);
    private static final int DURATION = 60;
    public static final WorkloadUpdateEventType WORKLOAD_UPDATE_EVENT_TYPE = WorkloadUpdateEventType.DELETE;

    @Mock
    private IWorkloadRepository workloadRepository;

    @InjectMocks
    private DeleteWorkloadEventConsumer testObject;

    @AfterEach
    void tearDown() {
        verifyNoMoreInteractions(workloadRepository);
    }

    @Test
    void getApplicationType_shouldReturnType() {
        var result = testObject.getApplicableType();

        assertEquals(WorkloadUpdateEventType.DELETE, result);
    }

    @Test
    void doConsume_shouldSubtractDurationAndSave_whenRemainingDurationIsPositive() {
        int existingDuration = DURATION + 30;
        var monthDocument = getMonthDocument(DATE.getMonth().getValue(), existingDuration);
        var yearDocument = getYearDocument(DATE.getYear(), new ArrayList<>(List.of(monthDocument)));
        var trainerDocument = getTrainerDocument(new ArrayList<>(List.of(yearDocument)));
        doReturn(Optional.of(trainerDocument)).when(workloadRepository).findById(USERNAME);

        testObject.doConsume(getEvent());

        verify(workloadRepository).findById(USERNAME);
        verify(workloadRepository).save(trainerDocument);

        assertEquals(existingDuration - DURATION, monthDocument.getTrainingSummaryDuration());
        assertTrue(yearDocument.getMonths().contains(monthDocument));
    }

    @Test
    void doConsume_shouldRemoveMonthAndSave_whenRemainingDurationIsZeroAndYearHasOtherMonths() {
        var targetMonth = getMonthDocument(DATE.getMonth().getValue(), DURATION);
        var otherMonth = getMonthDocument(DATE.getMonth().getValue() + 1, DURATION);
        var yearDocument = getYearDocument(DATE.getYear(), new ArrayList<>(List.of(targetMonth, otherMonth)));
        var trainerDocument = getTrainerDocument(new ArrayList<>(List.of(yearDocument)));
        doReturn(Optional.of(trainerDocument)).when(workloadRepository).findById(USERNAME);

        testObject.doConsume(getEvent());

        verify(workloadRepository).findById(USERNAME);
        verify(workloadRepository).save(trainerDocument);

        assertFalse(yearDocument.getMonths().contains(targetMonth));
        assertTrue(yearDocument.getMonths().contains(otherMonth));
    }

    @Test
    void doConsume_shouldRemoveYearAndSave_whenYearBecomesEmptyAndDocumentHasOtherYears() {
        var monthDocument = getMonthDocument(DATE.getMonth().getValue(), DURATION);
        var targetYear = getYearDocument(DATE.getYear(), new ArrayList<>(List.of(monthDocument)));
        var otherYear = getYearDocument(DATE.getYear() - 1, new ArrayList<>(List.of(getMonthDocument(1, 30))));
        var trainerDocument = getTrainerDocument(new ArrayList<>(List.of(targetYear, otherYear)));
        doReturn(Optional.of(trainerDocument)).when(workloadRepository).findById(USERNAME);

        testObject.doConsume(getEvent());

        verify(workloadRepository).findById(USERNAME);
        verify(workloadRepository).save(trainerDocument);

        assertFalse(trainerDocument.getYears().contains(targetYear));
        assertTrue(trainerDocument.getYears().contains(otherYear));
    }

    @Test
    void doConsume_shouldDeleteDocument_whenNoYearsRemainAfterCleanup() {
        var event = getEvent();
        var monthDocument = getMonthDocument(DATE.getMonth().getValue(), DURATION);
        var yearDocument = getYearDocument(DATE.getYear(), new ArrayList<>(List.of(monthDocument)));
        var trainerDocument = getTrainerDocument(new ArrayList<>(List.of(yearDocument)));
        doReturn(Optional.of(trainerDocument)).when(workloadRepository).findById(USERNAME);

        testObject.doConsume(event);

        verify(workloadRepository).findById(USERNAME);
        verify(workloadRepository).delete(trainerDocument);
    }


    @Test
    void doConsume_shouldThrowTrainingNotFoundException_whenDocumentNotFound() {
        var event = getEvent();
        doReturn(Optional.empty()).when(workloadRepository).findById(USERNAME);

        assertThrows(TrainingNotFoundException.class, () -> testObject.doConsume(event));

        verify(workloadRepository).findById(USERNAME);
    }

    @Test
    void doConsume_shouldThrowTrainingNotFoundException_whenYearNotFound() {
        var event = getEvent();
        var yearDocument = getYearDocument(DATE.getYear() + 1, new ArrayList<>());
        var trainerDocument = getTrainerDocument(new ArrayList<>(List.of(yearDocument)));
        doReturn(Optional.of(trainerDocument)).when(workloadRepository).findById(USERNAME);

        assertThrows(TrainingNotFoundException.class, () -> testObject.doConsume(event));

        verify(workloadRepository).findById(USERNAME);
    }

    @Test
    void doConsume_shouldThrowTrainingNotFoundException_whenMonthNotFound() {
        var event = getEvent();
        var monthDocument = getMonthDocument(DATE.getMonth().getValue() + 1, DURATION);
        var yearDocument = getYearDocument(DATE.getYear(), new ArrayList<>(List.of(monthDocument)));
        var trainerDocument = getTrainerDocument(new ArrayList<>(List.of(yearDocument)));
        doReturn(Optional.of(trainerDocument)).when(workloadRepository).findById(USERNAME);

        assertThrows(TrainingNotFoundException.class, () -> testObject.doConsume(event));

        verify(workloadRepository).findById(USERNAME);
    }

    @ParameterizedTest
    @NullSource
    void doConsume_shouldThrowException_whenArgumentNull(WorkloadUpdateEvent event) {
        assertThrows(NullPointerException.class, () -> testObject.doConsume(event));
    }


    private static WorkloadUpdateEvent getEvent() {
        return WorkloadUpdateEvent.builder()
            .trainerUsername(USERNAME)
            .trainingDate(DATE)
            .trainingDuration(DURATION)
            .eventType(WORKLOAD_UPDATE_EVENT_TYPE)
            .build();
    }

    private static TrainerDocument getTrainerDocument(List<YearDocument> years) {
        return TrainerDocument.builder()
            .username(USERNAME)
            .years(years)
            .build();
    }

    private static YearDocument getYearDocument(int year, List<MonthDocument> months) {
        return YearDocument.builder()
            .year(year)
            .months(months)
            .build();
    }

    private static MonthDocument getMonthDocument(int month, int duration) {
        return MonthDocument.builder()
            .month(month)
            .trainingSummaryDuration(duration)
            .build();
    }
}
