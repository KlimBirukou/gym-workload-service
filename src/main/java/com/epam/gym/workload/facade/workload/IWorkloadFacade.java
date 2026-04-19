package com.epam.gym.workload.facade.workload;

import com.epam.gym.workload.controller.rest.dto.TrainerWorkloadResponse;
import lombok.NonNull;

public interface IWorkloadFacade {

    TrainerWorkloadResponse getWorkload(@NonNull String username);
}
