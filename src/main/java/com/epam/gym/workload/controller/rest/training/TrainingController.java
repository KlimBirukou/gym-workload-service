package com.epam.gym.workload.controller.rest.training;

import com.epam.gym.workload.controller.rest.dto.TrainingRequest;
import com.epam.gym.workload.facade.training.ITrainingFacade;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/internal/v1/workload")
@RequiredArgsConstructor
public class TrainingController {

    private final ITrainingFacade workloadFacade;

    @PostMapping
    public void updateWorkload(@RequestBody TrainingRequest request) {
        workloadFacade.updateWorkload(request);
    }
}
