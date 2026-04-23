package com.epam.gym.workload.service.event.consume;

import com.epam.gym.workload.domain.update.WorkloadUpdateEvent;
import com.epam.gym.workload.domain.update.WorkloadUpdateEventType;
import com.epam.gym.workload.repository.IWorkloadRepository;
import com.epam.gym.workload.repository.entity.MonthDocument;
import com.epam.gym.workload.repository.entity.TrainerDocument;
import com.epam.gym.workload.repository.entity.YearDocument;
import com.epam.gym.workload.service.event.consumer.AddWorkloadEventConsumer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

@ExtendWith(MockitoExtension.class)
class AddWorkloadEventConsumerTest {

    private static final String USERNAME = "username";
    private static final LocalDate DATE = LocalDate.of(2026, 6, 6);
    private static final int DURATION = 60;
    public static final WorkloadUpdateEventType WORKLOAD_UPDATE_EVENT_TYPE = WorkloadUpdateEventType.ADD;

    @Mock
    private IWorkloadRepository workloadRepository;

    @Captor
    private ArgumentCaptor<TrainerDocument> trainerDocumentCaptor;

    @InjectMocks
    private AddWorkloadEventConsumer testObject;

    @AfterEach
    void tearDown() {
        verifyNoMoreInteractions(workloadRepository);
    }

    @Test
    void getApplicationType_shouldReturnType() {
        var result = testObject.getApplicableType();

        assertEquals(WorkloadUpdateEventType.ADD, result);
    }

    @Test
    void doConsume_shouldCreateNewTrainerDocument_whenNotExistForTrainer() {
        var event = getEvent();
        doReturn(Optional.empty()).when(workloadRepository).findById(USERNAME);

        testObject.doConsume(event);

        verify(workloadRepository).save(trainerDocumentCaptor.capture());
        var trainerDocument = trainerDocumentCaptor.getValue();
        var yearDocument = trainerDocument.getYears().getFirst();
        var monthDocument = yearDocument.getMonths().getFirst();

        assertEquals(USERNAME, trainerDocument.getUsername());
        assertEquals(1, trainerDocument.getYears().size());
        assertEquals(DATE.getYear(), yearDocument.getYear());
        assertEquals(1, yearDocument.getMonths().size());
        assertEquals(DATE.getMonth().getValue(), monthDocument.getMonth());
        assertEquals(DURATION, monthDocument.getTrainingSummaryDuration());
    }

    @Test
    void doConsume_shouldCreateNewYearDocument_whenTrainerDocumentExist() {
        var event = getEvent();
        var otherYear = getYearDocument(DATE.getYear() - 1, new ArrayList<>());
        var trainerDocument = getTrainerDocument(new ArrayList<>(List.of(otherYear)));
        doReturn(Optional.of(trainerDocument)).when(workloadRepository).findById(USERNAME);

        testObject.doConsume(event);

        verify(workloadRepository).findById(USERNAME);
        verify(workloadRepository).save(trainerDocumentCaptor.capture());
        var saved = trainerDocumentCaptor.getValue();
        var newYear = saved.getYears().stream()
            .filter(y -> y.getYear() == DATE.getYear())
            .findFirst()
            .orElseThrow();
        var monthDocument = newYear.getMonths().getFirst();

        assertEquals(2, saved.getYears().size());
        assertEquals(DATE.getYear(), newYear.getYear());
        assertEquals(1, newYear.getMonths().size());
        assertEquals(DATE.getMonth().getValue(), monthDocument.getMonth());
        assertEquals(DURATION, monthDocument.getTrainingSummaryDuration());

    }

    @Test
    void doConsume_shouldCreateNewYearMonth_whenYearDocumentExist() {
        var event = getEvent();
        var otherMonth = getMonthDocument(DATE.getMonth().getValue() - 1, DURATION);
        var yearDocument = getYearDocument(DATE.getYear(), new ArrayList<>(List.of(otherMonth)));
        var trainerDocument = getTrainerDocument(new ArrayList<>(List.of(yearDocument)));
        doReturn(Optional.of(trainerDocument)).when(workloadRepository).findById(USERNAME);

        testObject.doConsume(event);

        verify(workloadRepository).findById(USERNAME);
        verify(workloadRepository).save(trainerDocumentCaptor.capture());
        var savedYear = trainerDocumentCaptor.getValue().getYears().getFirst();
        var newMonth = savedYear.getMonths().stream()
            .filter(m -> m.getMonth() == DATE.getMonth().getValue())
            .findFirst()
            .orElseThrow();

        assertEquals(2, savedYear.getMonths().size());
        assertEquals(DATE.getMonth().getValue(), newMonth.getMonth());
        assertEquals(DURATION, newMonth.getTrainingSummaryDuration());

    }

    @Test
    void doConsume_shouldAddDuration_whenMonthDocumentExist() {
        int existingDuration = 30;
        var event = getEvent();
        var monthDocument = getMonthDocument(DATE.getMonth().getValue(), existingDuration);
        var yearDocument = getYearDocument(DATE.getYear(), new ArrayList<>(List.of(monthDocument)));
        var trainerDocument = getTrainerDocument(new ArrayList<>(List.of(yearDocument)));
        doReturn(Optional.of(trainerDocument)).when(workloadRepository).findById(USERNAME);

        testObject.doConsume(event);

        verify(workloadRepository).findById(USERNAME);
        verify(workloadRepository).save(trainerDocument);

        assertEquals(existingDuration + DURATION, monthDocument.getTrainingSummaryDuration());
    }

    @ParameterizedTest
    @NullSource
    void consume_shouldThrowException_whenEArgumentNull(WorkloadUpdateEvent event) {
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
