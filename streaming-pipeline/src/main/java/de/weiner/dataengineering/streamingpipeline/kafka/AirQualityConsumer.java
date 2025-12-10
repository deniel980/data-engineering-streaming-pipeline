package de.weiner.dataengineering.streamingpipeline.kafka;

import de.weiner.dataengineering.streamingpipeline.mongo.AirQualityRecord;
import de.weiner.dataengineering.streamingpipeline.mongo.AirQualityRecordRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class AirQualityConsumer {

    private static final Logger log = LoggerFactory.getLogger(AirQualityConsumer.class);

    private final AirQualityRecordRepository repository;

    public AirQualityConsumer(AirQualityRecordRepository repository) {
        this.repository = repository;
    }

    @KafkaListener(
            topics = "${app.kafka.topic}",
            groupId = "${spring.kafka.consumer.group-id}"
    )
    public void listen(String message) {
        log.info("Received message from Kafka: {}", message);

        AirQualityRecord record =
                new AirQualityRecord(message, Instant.now());
        repository.save(record);

        log.info("Saved message to MongoDB with id={}", record.getId());
    }
}