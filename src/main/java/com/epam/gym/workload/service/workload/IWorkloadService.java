package com.epam.gym.workload.service.workload;

import com.epam.gym.workload.controller.rest.dto.TrainerWorkloadResponse;
import lombok.NonNull;

public interface IWorkloadService {

    TrainerWorkloadResponse getWorkload(@NonNull String username);
}
