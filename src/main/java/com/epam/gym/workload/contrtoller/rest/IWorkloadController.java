package com.epam.gym.workload.contrtoller.rest;

import com.epam.gym.workload.contrtoller.rest.dto.TrainingWorkloadRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;

@RequestMapping("/api/v1/workload")
public interface IWorkloadController {

    @PostMapping(
        consumes = MediaType.APPLICATION_JSON_VALUE
    )
    @ResponseStatus(HttpStatus.OK)
    void updateWorkload(@Valid @RequestBody TrainingWorkloadRequest request);
}
