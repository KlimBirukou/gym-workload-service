package com.epam.gym.workload.service.training;

import com.epam.gym.workload.listener.WorkloadUpdateEvent;
import lombok.NonNull;

public interface ITrainingService {

    void updateWorkload(@NonNull WorkloadUpdateEvent request);
}
