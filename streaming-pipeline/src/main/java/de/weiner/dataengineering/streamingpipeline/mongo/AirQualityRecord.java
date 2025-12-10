package de.weiner.dataengineering.streamingpipeline.mongo;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Document(collection = "air_quality_events")   // MongoDB collection name
public class AirQualityRecord {

    @Id
    private String id;

    private String message;
    private Instant receivedAt;

    public AirQualityRecord(String message, Instant receivedAt) {
        this.message = message;
        this.receivedAt = receivedAt;
    }

    // Spring Data needs a no-args constructor
    protected AirQualityRecord() {
    }

    public String getId() {
        return id;
    }

    public String getMessage() {
        return message;
    }

    public Instant getReceivedAt() {
        return receivedAt;
    }
}