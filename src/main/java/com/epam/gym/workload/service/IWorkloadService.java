package com.epam.gym.workload.service;

import com.epam.gym.workload.contrtoller.rest.dto.TrainingWorkloadRequest;
import lombok.NonNull;

public interface IWorkloadService {

    void updateWorkload(@NonNull TrainingWorkloadRequest request);
}
