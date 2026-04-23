package com.epam.gym.workload.service.event.consumer;

import com.epam.gym.workload.domain.update.WorkloadUpdateEventType;
import com.epam.gym.workload.domain.update.WorkloadUpdateEvent;
import lombok.NonNull;

public interface IWorkloadUpdateEventConsumer {

    void consume(@NonNull WorkloadUpdateEvent event);

    WorkloadUpdateEventType getApplicableType();
}
