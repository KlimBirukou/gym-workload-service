package com.epam.gym.workload.facade.event;

import com.epam.gym.workload.domain.update.WorkloadUpdateEvent;
import lombok.NonNull;

public interface IEventFacade {

    void updateWorkload(@NonNull WorkloadUpdateEvent event);
}
