package com.epam.gym.workload.contrtoller.rest;

import com.epam.gym.workload.contrtoller.rest.dto.TrainingWorkloadRequest;
import com.epam.gym.workload.facade.IWorkloadFacade;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class WorkloadController implements IWorkloadController{

    private final IWorkloadFacade workloadFacade;

    @Override
    public void updateWorkload(TrainingWorkloadRequest request) {
        workloadFacade.updateWorkload(request);
    }
}
