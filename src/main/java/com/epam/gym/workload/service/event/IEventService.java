package com.epam.gym.workload.service.event;

import com.epam.gym.workload.domain.update.WorkloadUpdateEvent;
import lombok.NonNull;

public interface IEventService {

    void updateWorkload(@NonNull WorkloadUpdateEvent event);
}
