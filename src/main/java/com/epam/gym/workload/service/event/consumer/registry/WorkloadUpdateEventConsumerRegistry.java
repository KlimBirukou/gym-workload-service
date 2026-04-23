package com.epam.gym.workload.service.event.consumer.registry;

import com.epam.gym.workload.domain.update.WorkloadUpdateEventType;
import com.epam.gym.workload.service.event.consumer.IWorkloadUpdateEventConsumer;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.EnumMap;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class WorkloadUpdateEventConsumerRegistry implements IWorkloadUpdateEventConsumerRegistry {

    private final Collection<IWorkloadUpdateEventConsumer> consumers;
    private final Map<WorkloadUpdateEventType, IWorkloadUpdateEventConsumer> eventTypeToConsumer
        = new EnumMap<>(WorkloadUpdateEventType.class);

    @PostConstruct
    public void setUp() {
        consumers.forEach(consumer -> eventTypeToConsumer.put(consumer.getApplicableType(), consumer));
    }

    @Override
    public Optional<IWorkloadUpdateEventConsumer> get(WorkloadUpdateEventType eventType) {
        return Optional.ofNullable(eventType)
            .map(eventTypeToConsumer::get);
    }
}
