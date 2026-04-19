package com.epam.gym.workload.facade.training;

import com.epam.gym.workload.listener.WorkloadUpdateEvent;
import lombok.NonNull;

public interface ITrainingFacade {

    void updateWorkload(@NonNull WorkloadUpdateEvent request);
}
