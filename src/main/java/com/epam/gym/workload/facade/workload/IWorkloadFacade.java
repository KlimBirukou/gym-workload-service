package com.epam.gym.workload.facade.workload;

import com.epam.gym.workload.domain.workload.TrainerWorkload;
import lombok.NonNull;

public interface IWorkloadFacade {

    TrainerWorkload getWorkload(@NonNull String username);
}
