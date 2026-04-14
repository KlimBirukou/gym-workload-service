package com.epam.gym.workload.repository;

import lombok.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface IWorkloadRepository extends JpaRepository<@NonNull TrainingEntity, @NonNull UUID> {

    boolean existsByUsernameAndDate(@NonNull String username, @NonNull LocalDate date);

    Optional<TrainingEntity> findByUsernameAndDate(@NonNull String username, @NonNull LocalDate date);

    List<TrainingEntity> findAllByUsername(@NonNull String username);
}
