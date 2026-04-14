package com.epam.gym.workload.service.training;

import com.epam.gym.workload.controller.rest.dto.TrainingRequest;
import lombok.NonNull;

public interface ITrainingService {

    void updateWorkload(@NonNull TrainingRequest request);
}
