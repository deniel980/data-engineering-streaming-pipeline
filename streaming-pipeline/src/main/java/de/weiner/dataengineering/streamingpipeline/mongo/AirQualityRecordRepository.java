package de.weiner.dataengineering.streamingpipeline.mongo;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface AirQualityRecordRepository
        extends MongoRepository<AirQualityRecord, String> {
    // we don't need any extra methods yet
}