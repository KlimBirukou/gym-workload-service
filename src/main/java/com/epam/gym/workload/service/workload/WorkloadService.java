package com.epam.gym.workload.service.workload;

import com.epam.gym.workload.controller.rest.dto.MonthWorkloadResponse;
import com.epam.gym.workload.controller.rest.dto.TrainerWorkloadResponse;
import com.epam.gym.workload.controller.rest.dto.YearWorkloadResponse;
import com.epam.gym.workload.domain.Training;
import com.epam.gym.workload.exception.TrainerNotFoundException;
import com.epam.gym.workload.repository.IWorkloadRepository;
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
    public TrainerWorkloadResponse getWorkload(@NonNull String username) {
        var trainings = fetchTrainings(username);
        var years = groupByYears(trainings);
        return TrainerWorkloadResponse.builder()
            .username(username)
            .years(years)
            .build();
    }

    private List<Training> fetchTrainings(String username) {
        return Optional.of(workloadRepository.findAllByUsername(username))
            .filter(list -> !list.isEmpty())
            .orElseThrow(() -> new TrainerNotFoundException(username))
            .stream()
            .map(entity -> conversionService.convert(entity, Training.class))
            .toList();
    }

    private List<YearWorkloadResponse> groupByYears(List<Training> trainings) {
        return trainings.stream()
            .collect(Collectors.groupingBy(
                t -> t.getDate().getYear(),
                Collectors.groupingBy(
                    t -> t.getDate().getMonth(),
                    Collectors.summingInt(Training::getDuration)
                )
            ))
            .entrySet().stream()
            .sorted(Map.Entry.comparingByKey())
            .map(entry -> YearWorkloadResponse.builder()
                .year(entry.getKey())
                .months(groupByMonths(entry.getValue()))
                .build())
            .toList();
    }

    private List<MonthWorkloadResponse> groupByMonths(Map<Month, Integer> monthMap) {
        return monthMap.entrySet().stream()
            .sorted(Map.Entry.comparingByKey())
            .map(entry -> MonthWorkloadResponse.builder()
                .month(entry.getKey())
                .totalDuration(entry.getValue())
                .build())
            .toList();
    }
}
