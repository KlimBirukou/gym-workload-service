package com.epam.gym.workload.listener;

import com.epam.gym.workload.configuration.properties.RequestUidProperties;
import com.epam.gym.workload.facade.training.ITrainingFacade;
import io.micrometer.common.util.StringUtils;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.MDC;
import org.springframework.kafka.annotation.DltHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.kafka.annotation.BackOff;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class WorkloadUpdateEventListener implements IWorkloadUpdateEventListener {

    private final ITrainingFacade trainingFacade;
    private final RequestUidProperties requestUidProperties;

    @RetryableTopic(
        attempts = "3",
        backOff = @BackOff(delay = 1000, multiplier = 2.0, maxDelay = 10000),
        dltTopicSuffix = ".DLT",
        autoCreateTopics = "false"
    )
    @KafkaListener(
        topics = "${application.messaging.topics.trainer-workload}",
        containerFactory = "kafkaListenerContainerFactory"
    )
    public void onMessage(@NonNull ConsumerRecord<String, WorkloadUpdateEvent> consumerRecord) {
        var uid = extractRequestUid(consumerRecord);
        try (var ignored = MDC.putCloseable(requestUidProperties.mdcKey(), uid)) {
            var training = consumerRecord.value();
            log.info(
                "Message received. Trainer={}, Date={}, Duration={}, Action={}",
                training.trainerUsername(),
                training.trainingDate(),
                training.trainingDuration(),
                training.actionType()
            );
            trainingFacade.updateWorkload(consumerRecord.value());
            log.info("Message processed. Trainer={}", training.trainerUsername());
        }
    }

    @DltHandler
    public void onDltMessage(ConsumerRecord<String, WorkloadUpdateEvent> consumerRecord) {
        log.error(
            "Message failed all retries → DLT. Trainer={}, Topic={}, Partition={}, Offset={}",
            Objects.nonNull(consumerRecord.value())
                ? consumerRecord.value().trainerUsername()
                : "unknown",
            consumerRecord.topic(),
            consumerRecord.partition(),
            consumerRecord.offset()
        );
    }

    private String extractRequestUid(ConsumerRecord<?, ?> consumerRecord) {
        var header = consumerRecord.headers().lastHeader(requestUidProperties.headerName());
        return Optional.ofNullable(header)
            .map(h -> new String(h.value(), StandardCharsets.UTF_8))
            .filter(StringUtils::isNotBlank)
            .orElseGet(() -> UUID.randomUUID().toString());
    }
}
