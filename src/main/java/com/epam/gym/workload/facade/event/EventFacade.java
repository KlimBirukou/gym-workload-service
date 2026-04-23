package com.epam.gym.workload.facade.event;

import com.epam.gym.workload.domain.update.WorkloadUpdateEvent;
import com.epam.gym.workload.service.event.IEventService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class EventFacade implements IEventFacade {

    private final IEventService eventService;

    @Override
    public void updateWorkload(@NonNull WorkloadUpdateEvent event) {
        log.info("Update workload. Started. Action={}. Trainee username={}, date={}, duration={}",
            event.eventType(), event.trainerUsername(), event.trainingDate(), event.trainingDuration()
        );
        eventService.updateWorkload(event);
        log.info("Update workload. Finished. Action={}. Trainee username={}, date={}, duration={}",
            event.eventType(), event.trainerUsername(), event.trainingDate(), event.trainingDuration()
        );
    }
}
