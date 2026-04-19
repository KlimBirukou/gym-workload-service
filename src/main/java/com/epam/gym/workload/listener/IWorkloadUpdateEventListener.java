package com.epam.gym.workload.listener;

import lombok.NonNull;
import org.apache.kafka.clients.consumer.ConsumerRecord;

public interface IWorkloadUpdateEventListener {

    void onMessage(@NonNull ConsumerRecord<String, WorkloadUpdateEvent> consumerRecord);
}
