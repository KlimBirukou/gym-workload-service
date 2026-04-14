package com.epam.gym.workload.controller.rest.workload;

import com.epam.gym.workload.controller.rest.dto.WorkloadResponse;
import com.epam.gym.workload.facade.workload.IWorkloadFacade;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class WorkloadController implements IWorkloadController {

    private final IWorkloadFacade workloadFacade;

    @Override
    public WorkloadResponse getStatistic(String username) {
        return workloadFacade.getWorkload(username);
    }
}
