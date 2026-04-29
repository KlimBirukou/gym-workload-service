package com.epam.gym.workload.hooks;

import com.epam.gym.workload.client.IAuthClient;
import com.epam.gym.workload.client.ValidateResponse;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import lombok.RequiredArgsConstructor;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;

@RequiredArgsConstructor
public class CucumberHooks {

    private final IAuthClient authClient;
    private final DatabaseCleanupHook cleanupHook;

    @Before
    public void setUp() {
        doReturn(ValidateResponse.valid("it-test-user")).when(authClient).validate(anyString());
        cleanupHook.cleanup();
    }
}
