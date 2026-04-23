package com.epam.gym.workload.facade.workload;

import com.epam.gym.workload.domain.workload.TrainerWorkload;
import com.epam.gym.workload.service.workload.IWorkloadService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class WorkloadFacade implements IWorkloadFacade {

    private final IWorkloadService workloadService;

    @Override
    public TrainerWorkload getWorkload(@NonNull String username) {
        log.info("Get workload. Started. Username={}", username);
        var result = workloadService.getWorkload(username);
        log.info("Get statistic. Finished. Workload={}", result);
        return result;
    }
}
