package com.epam.gym.workload.service.event;

import com.epam.gym.workload.domain.update.WorkloadUpdateEvent;
import com.epam.gym.workload.service.event.consumer.registry.IWorkloadUpdateEventConsumerRegistry;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EventService implements IEventService {

    private final IWorkloadUpdateEventConsumerRegistry workloadUpdateEventConsumerRegistry;

    @Override
    public void updateWorkload(@NonNull WorkloadUpdateEvent event) {
        workloadUpdateEventConsumerRegistry.get(event.eventType())
            .ifPresentOrElse(
                consumer -> consumer.consume(event),
                () -> {
                    throw new UnsupportedOperationException();
                }
            );
    }
}
