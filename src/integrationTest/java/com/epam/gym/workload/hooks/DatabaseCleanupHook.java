package com.epam.gym.workload.hooks;

import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;

@Component
public class DatabaseCleanupHook {

    private final MongoTemplate mongoTemplate;

    public DatabaseCleanupHook(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    public void cleanup() {
        mongoTemplate.getCollectionNames()
            .forEach(mongoTemplate::dropCollection);
    }
}
