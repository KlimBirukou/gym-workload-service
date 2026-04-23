package com.epam.gym.workload.repository;

import com.epam.gym.workload.repository.entity.TrainerDocument;
import lombok.NonNull;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IWorkloadRepository extends MongoRepository<@NonNull TrainerDocument, @NonNull String> {

}
