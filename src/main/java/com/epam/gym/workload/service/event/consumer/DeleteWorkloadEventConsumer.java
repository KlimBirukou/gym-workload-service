package com.epam.gym.workload.service.event.consumer;

import com.epam.gym.workload.domain.update.WorkloadUpdateEventType;
import com.epam.gym.workload.exception.TrainingNotFoundException;
import com.epam.gym.workload.domain.update.WorkloadUpdateEvent;
import com.epam.gym.workload.repository.IWorkloadRepository;
import com.epam.gym.workload.repository.entity.MonthDocument;
import com.epam.gym.workload.repository.entity.TrainerDocument;
import com.epam.gym.workload.repository.entity.YearDocument;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class DeleteWorkloadEventConsumer
    extends AbstractWorkloadEventConsumer
    implements IWorkloadUpdateEventConsumer {

    private final IWorkloadRepository workloadRepository;

    @Override
    public WorkloadUpdateEventType getApplicableType() {
        return WorkloadUpdateEventType.DELETE;
    }

    @Override
    public void doConsume(@NonNull WorkloadUpdateEvent event) {
        var username = event.trainerUsername();
        var date = event.trainingDate();
        var document = workloadRepository.findById(username)
            .orElseThrow(() -> new TrainingNotFoundException(username, date));
        var year = findYear(document, username, date);
        var month = findMonth(year, username, date);
        month.setTrainingSummaryDuration(month.getTrainingSummaryDuration() - event.trainingDuration());
        if (month.getTrainingSummaryDuration() <= 0) {
            year.getMonths().remove(month);
        }
        document.getYears().removeIf(y -> y.getMonths().isEmpty());
        if (document.getYears().isEmpty()) {
            workloadRepository.delete(document);
        } else {
            workloadRepository.save(document);
        }
    }

    private YearDocument findYear(TrainerDocument document, String username, LocalDate date) {
        return document.getYears().stream()
            .filter(y -> y.getYear() == date.getYear())
            .findFirst()
            .orElseThrow(() -> new TrainingNotFoundException(username, date));
    }

    private MonthDocument findMonth(YearDocument year, String username, LocalDate date) {
        return year.getMonths().stream()
            .filter(m -> m.getMonth() == date.getMonthValue())
            .findFirst()
            .orElseThrow(() -> new TrainingNotFoundException(username, date));
    }
}
