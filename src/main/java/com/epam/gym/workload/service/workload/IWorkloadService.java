package com.epam.gym.workload.service.workload;

import com.epam.gym.workload.controller.rest.dto.WorkloadResponse;
import lombok.NonNull;

public interface IWorkloadService {

    WorkloadResponse getWorkload(@NonNull String username);
}
