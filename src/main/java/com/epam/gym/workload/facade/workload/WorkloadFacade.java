package com.epam.gym.workload.facade.workload;

import com.epam.gym.workload.controller.rest.dto.WorkloadResponse;
import com.epam.gym.workload.service.workload.IWorkloadService;
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
    @Transactional(readOnly = true)
    public WorkloadResponse getWorkload(@NonNull String username) {
        log.info("Get workload. Started. Username={}", username);
        var result = workloadService.getWorkload(username);
        log.info("Get statistic. Finished. Workload={}", result);
        return result;
    }
}
