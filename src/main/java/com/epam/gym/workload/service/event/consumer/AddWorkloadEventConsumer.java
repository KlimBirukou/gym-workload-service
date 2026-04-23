package com.epam.gym.workload.service.event.consumer;

import com.epam.gym.workload.domain.update.WorkloadUpdateEventType;
import com.epam.gym.workload.domain.update.WorkloadUpdateEvent;
import com.epam.gym.workload.repository.IWorkloadRepository;
import com.epam.gym.workload.repository.entity.MonthDocument;
import com.epam.gym.workload.repository.entity.TrainerDocument;
import com.epam.gym.workload.repository.entity.YearDocument;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AddWorkloadEventConsumer
    extends AbstractWorkloadEventConsumer
    implements IWorkloadUpdateEventConsumer {

    private final IWorkloadRepository workloadRepository;

    @Override
    public WorkloadUpdateEventType getApplicableType() {
        return WorkloadUpdateEventType.ADD;
    }

    @Override
    public void doConsume(@NonNull WorkloadUpdateEvent event) {
        var username = event.trainerUsername();
        int yearNum = event.trainingDate().getYear();
        int monthNum = event.trainingDate().getMonthValue();
        var document = workloadRepository.findById(username)
            .orElseGet(() -> TrainerDocument.builder()
                .username(username)
                .build()
            );
        var year = findOrCreateYear(document, yearNum);
        var month = findOrCreateMonth(year, monthNum);
        month.setTrainingSummaryDuration(month.getTrainingSummaryDuration() + event.trainingDuration());
        workloadRepository.save(document);
    }

    private YearDocument findOrCreateYear(TrainerDocument document, int yearNum) {
        return document.getYears().stream()
            .filter(y -> y.getYear() == yearNum)
            .findFirst()
            .orElseGet(() -> {
                var newYear = YearDocument.builder().year(yearNum).build();
                document.getYears().add(newYear);
                return newYear;
            });
    }

    private MonthDocument findOrCreateMonth(YearDocument year, int monthNum) {
        return year.getMonths().stream()
            .filter(m -> m.getMonth() == monthNum)
            .findFirst()
            .orElseGet(() -> {
                var newMonth = MonthDocument.builder().month(monthNum).build();
                year.getMonths().add(newMonth);
                return newMonth;
            });
    }
}
