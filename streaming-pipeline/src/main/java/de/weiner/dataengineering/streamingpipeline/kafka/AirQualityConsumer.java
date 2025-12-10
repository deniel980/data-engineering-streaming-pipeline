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

        String[] parts = message.split(",", -1);

        if (parts.length != 12) {
            log.warn("Skipping message with unexpected column count {}: {}", parts.length, message);
            return;
        }

        String timestamp = parts[0].trim();
        double temperature     = parseDoubleSafe(parts[1]);
        double humidity        = parseDoubleSafe(parts[2]);
        double co2             = parseDoubleSafe(parts[3]);
        double pm25            = parseDoubleSafe(parts[4]);
        double pm10            = parseDoubleSafe(parts[5]);
        double tvoc            = parseDoubleSafe(parts[6]);
        double co              = parseDoubleSafe(parts[7]);
        double lightIntensity  = parseDoubleSafe(parts[8]);
        boolean motionDetected = "1".equals(parts[9].trim());
        int occupancyCount     = parseIntSafe(parts[10]);
        String ventilationStatus = parts[11].trim();

        AirQualityRecord record = new AirQualityRecord(
                message,
                timestamp,
                temperature,
                humidity,
                co2,
                pm25,
                pm10,
                tvoc,
                co,
                lightIntensity,
                motionDetected,
                occupancyCount,
                ventilationStatus,
                Instant.now()
        );

        repository.save(record);
        log.info("Saved parsed record to MongoDB with id={}", record.getId());
    }

    private double parseDoubleSafe(String value) {
        if (value == null) return 0.0;
        String v = value.trim();
        if (v.isEmpty()) return 0.0;
        return Double.parseDouble(v);
    }

    private int parseIntSafe(String value) {
        if (value == null) return 0;
        String v = value.trim();
        if (v.isEmpty()) return 0;
        return Integer.parseInt(v);
    }
}