package com.epam.gym.workload.facade.training;

import com.epam.gym.workload.controller.rest.dto.TrainingRequest;
import com.epam.gym.workload.service.training.ITrainingService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class TrainingFacade implements ITrainingFacade {

    private final ITrainingService workloadService;

    @Override
    @Transactional
    public void updateWorkload(@NonNull TrainingRequest request) {
        log.info("Update workload. Started. Action={}. Trainee username={}, date={}, duration={}",
            request.actionType(), request.trainerUsername(), request.trainingDate(), request.trainingDuration()
        );
        workloadService.updateWorkload(request);
        log.info("Update workload. Finished. Action={}. Trainee username={}, date={}, duration={}",
            request.actionType(), request.trainerUsername(), request.trainingDate(), request.trainingDuration()
        );
    }
}
