package com.epam.gym.workload.configuration;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Clock;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(MockitoExtension.class)
class TimeConfigurationTest {

    @InjectMocks
    private TimeConfiguration testObject;

    @Test
    void clock_shouldReturnSystemDefaultZoneClock() {
        var result = testObject.clock();

        assertNotNull(result);
        assertInstanceOf(Clock.class, result);
    }
}
