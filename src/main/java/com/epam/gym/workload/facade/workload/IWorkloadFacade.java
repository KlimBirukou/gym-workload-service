package com.epam.gym.workload.facade.workload;

import com.epam.gym.workload.controller.rest.dto.WorkloadResponse;
import lombok.NonNull;

public interface IWorkloadFacade {

    WorkloadResponse getWorkload(@NonNull String username);
}
