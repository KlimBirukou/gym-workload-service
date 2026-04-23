package com.epam.gym.workload.listener;

import com.epam.gym.workload.domain.update.WorkloadUpdateEvent;
import lombok.NonNull;
import org.apache.kafka.clients.consumer.ConsumerRecord;

public interface IWorkloadUpdateEventListener {

    void onMessage(@NonNull ConsumerRecord<String, WorkloadUpdateEvent> consumerRecord);
}
