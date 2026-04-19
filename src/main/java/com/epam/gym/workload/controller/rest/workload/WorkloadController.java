package com.epam.gym.workload.controller.rest.workload;

import com.epam.gym.workload.controller.rest.dto.TrainerWorkloadResponse;
import com.epam.gym.workload.facade.workload.IWorkloadFacade;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class WorkloadController implements IWorkloadController {

    private final IWorkloadFacade workloadFacade;

    @Override
    public TrainerWorkloadResponse getStatistic(String username) {
        return workloadFacade.getWorkload(username);
    }
}
