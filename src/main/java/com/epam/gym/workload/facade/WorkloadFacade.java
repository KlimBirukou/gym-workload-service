package com.epam.gym.workload.facade;

import com.epam.gym.workload.contrtoller.rest.dto.TrainingWorkloadRequest;
import com.epam.gym.workload.service.IWorkloadService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class WorkloadFacade implements IWorkloadFacade {

    private final IWorkloadService workloadService;

    @Override
    @Transactional
    public void updateWorkload(@NonNull TrainingWorkloadRequest request) {
        log.info("Update workload. Started. Action={}. Trainee username={}, date={}, duration={}",
            request.actionType(), request.trainerUsername(), request.trainingDate(), request.trainingDuration()
        );
        workloadService.updateWorkload(request);
        log.info("Update workload. Finished. Action={}. Trainee username={}, date={}, duration={}",
            request.actionType(), request.trainerUsername(), request.trainingDate(), request.trainingDuration()
        );
    }
}
