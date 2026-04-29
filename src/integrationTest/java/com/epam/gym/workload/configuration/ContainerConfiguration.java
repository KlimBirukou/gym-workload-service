package com.epam.gym.workload.configuration;

import com.epam.gym.workload.client.IAuthClient;
import io.cucumber.spring.CucumberContextConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.kafka.ConfluentKafkaContainer;
import org.testcontainers.utility.DockerImageName;

@CucumberContextConfiguration
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("it")
@ContextConfiguration(initializers = ContainerConfiguration.Initializer.class)
public class ContainerConfiguration {

    private static final MongoDBContainer MONGO =
        new MongoDBContainer(DockerImageName.parse("mongo:8.0"));
    private static final ConfluentKafkaContainer KAFKA =
        new ConfluentKafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:7.6.1"));

    @MockitoBean
    private IAuthClient authClient;

    static {
        MONGO.start();
        KAFKA.start();
    }

    public static class Initializer
        implements ApplicationContextInitializer<ConfigurableApplicationContext> {

        @Override
        public void initialize(ConfigurableApplicationContext context) {
            TestPropertyValues.of(
                "spring.mongodb.uri=%s/gym_workload_database".formatted(MONGO.getConnectionString()),
                "spring.kafka.bootstrap-servers=%s".formatted(KAFKA.getBootstrapServers())
            ).applyTo(context.getEnvironment());
        }
    }
}
