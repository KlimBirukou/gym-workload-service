package com.epam.gym.workload.service.event.consumer.registry;

import com.epam.gym.workload.domain.update.WorkloadUpdateEventType;
import com.epam.gym.workload.service.event.consumer.IWorkloadUpdateEventConsumer;

import java.util.Optional;

public interface IWorkloadUpdateEventConsumerRegistry {

    Optional<IWorkloadUpdateEventConsumer> get(WorkloadUpdateEventType eventType);
}
