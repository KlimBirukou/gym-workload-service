package com.epam.gym.workload.facade;

import com.epam.gym.workload.contrtoller.rest.dto.TrainingWorkloadRequest;
import lombok.NonNull;

public interface IWorkloadFacade {

    void updateWorkload(@NonNull TrainingWorkloadRequest request);
}
