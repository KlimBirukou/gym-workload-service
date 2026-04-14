package com.epam.gym.workload.configuration;

import com.epam.gym.workload.configuration.properties.SecurityProperties;
import com.epam.gym.workload.configuration.properties.SwaggerProperties;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

@ExtendWith(MockitoExtension.class)
class SwaggerConfigurationTest {

    private static final String SERVER_URL = "http://localhost:8080";
    private static final String SERVER_DESCRIPTION = "Local server";
    private static final String PUBLIC_PREFIX = "/api/v1";

    @Mock
    private SwaggerProperties swaggerProperties;
    @Mock
    private SecurityProperties securityProperties;

    @InjectMocks
    private SwaggerConfiguration testObject;

    @AfterEach
    void tearDown() {
        verifyNoMoreInteractions(swaggerProperties, securityProperties);
    }

    @Test
    void publicApi_shouldReturnConfiguredGroupedOpenApi() {
        doReturn(PUBLIC_PREFIX).when(securityProperties).publicPrefix();

        var result = testObject.publicApi();

        assertNotNull(result);
        assertEquals("public-api", result.getGroup());
        verify(securityProperties).publicPrefix();
    }

    @Test
    void openAPI_shouldReturnConfiguredOpenAPI() {
        doReturn(SERVER_URL).when(swaggerProperties).serverUrl();
        doReturn(SERVER_DESCRIPTION).when(swaggerProperties).serverDescription();

        var result = testObject.openAPI();

        assertNotNull(result);
        assertNotNull(result.getInfo());
        assertEquals("Gym workload server API", result.getInfo().getTitle());
        assertEquals(1, result.getServers().size());
        assertEquals(SERVER_URL, result.getServers().getFirst().getUrl());
        assertEquals(SERVER_DESCRIPTION, result.getServers().getFirst().getDescription());
        var securitySchemes = result.getComponents().getSecuritySchemes();
        assertNotNull(securitySchemes.get("bearerAuth"));
        assertEquals("bearer", securitySchemes.get("bearerAuth").getScheme());
        verify(swaggerProperties).serverUrl();
        verify(swaggerProperties).serverDescription();
    }
}
