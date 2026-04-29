package com.epam.gym.workload.steps;

import com.epam.gym.workload.domain.update.WorkloadUpdateEvent;
import com.epam.gym.workload.domain.update.WorkloadUpdateEventType;
import com.epam.gym.workload.repository.IWorkloadRepository;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import lombok.RequiredArgsConstructor;
import org.awaitility.Awaitility;
import org.awaitility.core.ConditionFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.client.RestClient;

import java.time.LocalDate;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.skyscreamer.jsonassert.JSONAssert.assertEquals;

@RequiredArgsConstructor
public class WorkloadSteps {

    private static final int AWAIT_TIMEOUT_SECONDS = 30;
    private static final int POLL_INTERVAL_MS = 500;
    public static final String QUERY_PASS = "/api/v1/workload?username=";

    private final KafkaTemplate<String, WorkloadUpdateEvent> kafkaTemplate;
    private final IWorkloadRepository workloadRepository;
    private final RestClient.Builder restClientBuilder;

    @Value("${local.server.port}")
    private int port;
    @Value("${application.messaging.topics.trainer-workload}")
    private String topic;

    private ResponseEntity<String> lastResponse;

    @Given("Trainer {string} has no workload records")
    public void trainerHasNoWorkloadRecords(String username) {
        workloadRepository.deleteById(username);
    }

    @When("Workload is requested for trainer {string}")
    public void workloadIsRequestedForTrainer(String username) {
        lastResponse = buildClient().get()
            .uri(QUERY_PASS + username)
            .retrieve()
            .onStatus(HttpStatusCode::is4xxClientError, (req, res) -> {
            })
            .toEntity(String.class);
    }

    @When("Kafka {string} event is sent for trainer {string} with date {string} and duration {int}")
    public void sendAddEvent(String eventType, String username, String date, int duration) {
        var event = WorkloadUpdateEvent.builder()
            .trainerUsername(username)
            .trainingDate(LocalDate.parse(date))
            .trainingDuration(duration)
            .eventType(WorkloadUpdateEventType.valueOf(eventType))
            .build();
        kafkaTemplate.send(topic, username, event);
    }

    @Then("Response status should be {int}")
    public void responseStatusShouldBe(int expectedStatus) {
        assertThat(lastResponse.getStatusCode().value()).isEqualTo(expectedStatus);
    }

    @Then("Trainer {string} workload should match JSON:")
    public void workloadShouldMatchJson(String username, String expectedJson) {
        eventually().untilAsserted(() -> {
            var response = getWorkload(username);
            assertEquals(expectedJson, response.getBody(), false);
        });
    }

    @Then("Workload for trainer {string} should eventually be not found")
    public void workloadShouldEventuallyBeNotFound(String username) {
        eventually().untilAsserted(() -> {
            var response = getWorkload(username);
            assertThat(response.getStatusCode().value()).isEqualTo(404);
        });
    }

    private ConditionFactory eventually() {
        return Awaitility.await()
            .atMost(AWAIT_TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .pollInterval(POLL_INTERVAL_MS, TimeUnit.MILLISECONDS)
            .ignoreExceptions();
    }

    private ResponseEntity<String> getWorkload(String username) {
        return buildClient().get()
            .uri(QUERY_PASS + username)
            .retrieve()
            .onStatus(code -> true, (req, res) -> {
            })
            .toEntity(String.class);
    }

    private RestClient buildClient() {
        return restClientBuilder
            .baseUrl("http://localhost:" + port)
            .defaultHeader("Authorization", "Bearer it-test-token")
            .build();
    }
}
