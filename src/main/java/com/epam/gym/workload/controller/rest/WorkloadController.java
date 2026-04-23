package com.epam.gym.workload.controller.rest;

import com.epam.gym.workload.domain.workload.TrainerWorkload;
import com.epam.gym.workload.facade.workload.IWorkloadFacade;
import com.epam.gym.workload.metrics.annotation.Measured;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class WorkloadController implements IWorkloadController {

    private final IWorkloadFacade workloadFacade;

    @Override
    @Measured("GET_api_v1_workload")
    public TrainerWorkload getStatistic(String username) {
        return workloadFacade.getWorkload(username);
    }
}
