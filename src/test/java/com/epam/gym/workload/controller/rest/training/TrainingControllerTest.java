package com.epam.gym.workload.controller.rest.training;

import static org.mockito.Mockito.verify;

/*@ExtendWith(MockitoExtension.class)
class TrainingControllerTest {

    private static final String USERNAME = "username";
    private static final LocalDate DATE = LocalDate.of(2024, 6, 15);
    private static final int DURATION = 60;

    @Mock
    private ITrainingFacade workloadFacade;

    @InjectMocks
    private TrainingController testObject;

    @AfterEach
    void tearDown() {
        verifyNoMoreInteractions(workloadFacade);
    }

    @Test
    void updateWorkload() {
        var request = buildRequest();
        doNothing().when(workloadFacade).updateWorkload(request);

        testObject.updateWorkload(request);

        verify(workloadFacade).updateWorkload(request);
    }

    private static TrainingRequest buildRequest() {
        return new TrainingRequest(USERNAME, DATE, DURATION, ActionType.ADD);
    }
}*/
