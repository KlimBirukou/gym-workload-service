package com.epam.gym.workload.listener;

import com.epam.gym.workload.configuration.properties.RequestUidProperties;
import com.epam.gym.workload.domain.update.WorkloadUpdateEvent;
import com.epam.gym.workload.domain.update.WorkloadUpdateEventType;
import com.epam.gym.workload.facade.event.IEventFacade;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.header.internals.RecordHeader;
import org.apache.kafka.common.header.internals.RecordHeaders;
import org.apache.kafka.common.record.TimestampType;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class WorkloadUpdateEventListenerTest {

    private static final String TRAINER_USERNAME = "trainer_username";
    private static final String TOPIC = "topic";
    private static final String HEADER_NAME = "X-Request-Id";
    private static final String MDC_KEY = "requestUid";
    private static final String REQUEST_UID = "test-uid-1234";
    private static final LocalDate DATE = LocalDate.of(2026, 6, 6);
    private static final int DURATION = 120;

    @Mock
    private IEventFacade trainingFacade;
    @Mock
    private RequestUidProperties requestUidProperties;

    @InjectMocks
    private WorkloadUpdateEventListener testObject;

    @AfterEach
    void tearDown() {
        verifyNoMoreInteractions(trainingFacade, requestUidProperties);
    }

    @Test
    void onMessage_shouldProcessMessage_whenValidRecord() {
        var training = buildTrainingRequest();
        var consumerRecord = buildRecord(TOPIC, training, REQUEST_UID);
        when(requestUidProperties.headerName()).thenReturn(HEADER_NAME);
        when(requestUidProperties.mdcKey()).thenReturn(MDC_KEY);

        testObject.onMessage(consumerRecord);

        verify(trainingFacade).updateWorkload(training);
    }

    @Test
    void onMessage_shouldProcessMessage_whenHeaderMissing() {
        var training = buildTrainingRequest();
        var consumerRecord = buildRecordWithoutHeader(TOPIC, training);
        when(requestUidProperties.headerName()).thenReturn(HEADER_NAME);
        when(requestUidProperties.mdcKey()).thenReturn(MDC_KEY);

        testObject.onMessage(consumerRecord);

        verify(trainingFacade).updateWorkload(training);
    }

    @Test
    void onMessage_shouldProcessMessage_whenHeaderIsBlank() {
        var training = buildTrainingRequest();
        var consumerRecord = buildRecord(TOPIC, training, "   ");
        when(requestUidProperties.headerName()).thenReturn(HEADER_NAME);
        when(requestUidProperties.mdcKey()).thenReturn(MDC_KEY);

        testObject.onMessage(consumerRecord);

        verify(trainingFacade).updateWorkload(training);
    }

    @Test
    void onMessage_shouldPropagateException_whenFacadeThrows() {
        var training = buildTrainingRequest();
        var consumerRecord = buildRecord(TOPIC, training, REQUEST_UID);
        when(requestUidProperties.headerName()).thenReturn(HEADER_NAME);
        when(requestUidProperties.mdcKey()).thenReturn(MDC_KEY);
        doThrow(new RuntimeException()).when(trainingFacade).updateWorkload(training);

        assertThrows(RuntimeException.class, () -> testObject.onMessage(consumerRecord));

        verify(trainingFacade).updateWorkload(training);
    }

    @ParameterizedTest
    @NullSource
    void onMessage_shouldThrowException_whenArgumentIsNull(ConsumerRecord<String, WorkloadUpdateEvent> consumerRecord) {
        assertThrows(NullPointerException.class, () -> testObject.onMessage(consumerRecord));
    }

    @Test
    void onDltMessage_shouldLogError_whenMessageArrivesInDlt() {
        var training = buildTrainingRequest();
        var consumerRecord = buildRecord(TOPIC + ".DLT", training, REQUEST_UID);

        testObject.onDltMessage(consumerRecord);

        verifyNoInteractions(trainingFacade, requestUidProperties);
    }

    @Test
    void onDltMessage_shouldHandleNullValue_whenRecordValueIsNull() {
        var consumerRecord = buildRecord(TOPIC + ".DLT", null, REQUEST_UID);

        testObject.onDltMessage(consumerRecord);

        verifyNoInteractions(trainingFacade, requestUidProperties);
    }

    private static WorkloadUpdateEvent buildTrainingRequest() {
        return new WorkloadUpdateEvent(TRAINER_USERNAME, DATE, DURATION, WorkloadUpdateEventType.ADD);
    }

    private static ConsumerRecord<String, WorkloadUpdateEvent> buildRecord(String topic,
                                                                           WorkloadUpdateEvent value,
                                                                           String uid) {
        var headers = new RecordHeaders();
        headers.add(new RecordHeader(HEADER_NAME, uid.getBytes(StandardCharsets.UTF_8)));
        return buildConsumerRecord(topic, headers, value);
    }

    private static ConsumerRecord<String, WorkloadUpdateEvent> buildRecordWithoutHeader(String topic,
                                                                                        WorkloadUpdateEvent value) {
        return buildConsumerRecord(topic, new RecordHeaders(), value);
    }

    private static ConsumerRecord<String, WorkloadUpdateEvent> buildConsumerRecord(String topic,
                                                                                   RecordHeaders recordHeaders,
                                                                                   WorkloadUpdateEvent value) {
        return new ConsumerRecord<>(
            topic,
            0,
            0L,
            0L,
            TimestampType.CREATE_TIME,
            0,
            0,
            TRAINER_USERNAME,
            value,
            recordHeaders,
            Optional.empty());
    }
}
