package com.epam.gym.workload.facade.training;

import com.epam.gym.workload.controller.rest.dto.TrainingRequest;
import lombok.NonNull;

public interface ITrainingFacade {

    void updateWorkload(@NonNull TrainingRequest request);
}
