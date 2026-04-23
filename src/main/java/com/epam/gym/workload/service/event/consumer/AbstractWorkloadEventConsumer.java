package com.epam.gym.workload.service.event.consumer;

import com.epam.gym.workload.domain.update.WorkloadUpdateEvent;
import lombok.NonNull;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;

public abstract class AbstractWorkloadEventConsumer implements IWorkloadUpdateEventConsumer {

    @Override
    @Retryable(
        retryFor = OptimisticLockingFailureException.class,
        maxAttempts = 4,
        backoff = @Backoff(delay = 100, multiplier = 2)
    )
    public void consume(@NonNull WorkloadUpdateEvent event) {
        doConsume(event);
    }

    protected abstract void doConsume(@NonNull WorkloadUpdateEvent event);
}
