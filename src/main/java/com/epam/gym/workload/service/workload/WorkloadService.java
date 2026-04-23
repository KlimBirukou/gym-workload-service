package com.epam.gym.workload.service.workload;

import com.epam.gym.workload.domain.workload.TrainerWorkload;
import com.epam.gym.workload.exception.TrainerNotFoundException;
import com.epam.gym.workload.repository.IWorkloadRepository;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class WorkloadService implements IWorkloadService {

    private final IWorkloadRepository workloadRepository;
    private final IWorkloadMapper workloadMapper;

    @Override
    public TrainerWorkload getWorkload(@NonNull String username) {
        return workloadRepository.findById(username)
            .map(workloadMapper::toWorkload)
            .orElseThrow(() -> new TrainerNotFoundException(username));
    }
}
