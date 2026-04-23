package com.epam.gym.workload.service.workload;

import com.epam.gym.workload.domain.workload.TrainerWorkload;
import lombok.NonNull;

public interface IWorkloadService {

    TrainerWorkload getWorkload(@NonNull String username);
}
