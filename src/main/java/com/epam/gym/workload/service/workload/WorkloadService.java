package com.epam.gym.workload.service.workload;

import com.epam.gym.workload.controller.rest.dto.MonthStat;
import com.epam.gym.workload.controller.rest.dto.WorkloadResponse;
import com.epam.gym.workload.controller.rest.dto.YearStat;
import com.epam.gym.workload.domain.Training;
import com.epam.gym.workload.exception.TrainerNotFoundException;
import com.epam.gym.workload.repository.IWorkloadRepository;
import com.epam.gym.workload.repository.TrainingEntity;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Month;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class WorkloadService implements IWorkloadService{

    private final IWorkloadRepository workloadRepository;
    private final ConversionService conversionService;

    @Override
    @Transactional(readOnly = true)
    public WorkloadResponse getWorkload(@NonNull String username) {
        var trainings = Optional.of(workloadRepository.findAllByUsername(username))
            .filter(list -> !list.isEmpty())
            .orElseThrow(() -> new TrainerNotFoundException(username))
            .stream()
            .map(entity -> conversionService.convert(entity, Training.class))
            .toList();
        var years = trainings.stream()
            .collect(Collectors.groupingBy(
                t -> t.getDate().getYear(),
                Collectors.groupingBy(
                    t -> t.getDate().getMonth(),
                    Collectors.summingInt(Training::getDuration)
                )
            ))
            .entrySet().stream()
            .sorted(Map.Entry.comparingByKey())
            .map(yearEntry -> YearStat.builder()
                .year(yearEntry.getKey())
                .months(toMonthStats(yearEntry.getValue()))
                .build())
            .toList();
        return WorkloadResponse.builder()
            .username(username)
            .years(years)
            .build();
    }

    private List<MonthStat> toMonthStats(Map<Month, Integer> monthMap) {
        return monthMap.entrySet()
            .stream()
            .sorted(Map.Entry.comparingByKey())
            .map(e -> MonthStat.builder()
                .month(e.getKey())
                .totalDuration(e.getValue())
                .build())
            .toList();
    }
}
