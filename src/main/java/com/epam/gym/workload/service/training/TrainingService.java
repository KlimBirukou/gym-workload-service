package com.epam.gym.workload.service.training;

import com.epam.gym.workload.domain.ActionType;
import com.epam.gym.workload.controller.rest.dto.TrainingRequest;
import com.epam.gym.workload.domain.Training;
import com.epam.gym.workload.exception.TrainingAlreadyExistException;
import com.epam.gym.workload.exception.TrainingNotFoundException;
import com.epam.gym.workload.repository.IWorkloadRepository;
import com.epam.gym.workload.repository.TrainingEntity;
import jakarta.annotation.PostConstruct;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;

@Service
@RequiredArgsConstructor
public class TrainingService implements ITrainingService {

    private final IWorkloadRepository workloadRepository;
    private final ConversionService conversionService;

    private Map<ActionType, Consumer<Training>> handlers;

    @PostConstruct
    private void initHandlers() {
        handlers = Map.of(
            ActionType.ADD, this::add,
            ActionType.DELETE, this::delete
        );
    }

    @Override
    @Transactional
    public void updateWorkload(@NonNull TrainingRequest request) {
        var training = Training.builder()
            .duration(request.trainingDuration())
            .username(request.trainerUsername())
            .date(request.trainingDate())
            .build();

        handlers.get(request.actionType()).accept(training);
    }

    private void add(Training training) {
        if (workloadRepository.existsByUsernameAndDate(training.getUsername(), training.getDate())) {
            throw new TrainingAlreadyExistException(training.getUsername(), training.getDate());
        }
        var entity = conversionService.convert(training, TrainingEntity.class);
        entity.setUid(UUID.randomUUID());
        workloadRepository.save(entity);
    }

    private void delete(Training training) {
        var entity = workloadRepository
            .findByUsernameAndDate(training.getUsername(), training.getDate())
            .orElseThrow(() -> new TrainingNotFoundException(training.getUsername(), training.getDate()));
        workloadRepository.delete(entity);
    }
}
