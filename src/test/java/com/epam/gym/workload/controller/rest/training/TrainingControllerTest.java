package com.epam.gym.workload.controller.rest.workload;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class TrainingControllerTest {

    private static final String USERNAME = "trainer.john";
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

}
