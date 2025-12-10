package de.weiner.dataengineering.streamingpipeline.kafka;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class AirQualityProducer {

    private static final Logger log = LoggerFactory.getLogger(AirQualityProducer.class);

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final String topic;

    public AirQualityProducer(KafkaTemplate<String, String> kafkaTemplate,
                              @Value("${app.kafka.topic}") String topic) {
        this.kafkaTemplate = kafkaTemplate;
        this.topic = topic;
    }

    public void sendTestMessage(String payload) {
        log.info("Sending message to topic {}: {}", topic, payload);
        kafkaTemplate.send(topic, payload);
    }

    public void streamFromCsv() {
        try (var reader = java.nio.file.Files.newBufferedReader(
                java.nio.file.Path.of("data/IoT_Indoor_Air_Quality_Dataset.csv"))) {

            String header = reader.readLine(); // skip header

            String line;
            int count = 0;
            while ((line = reader.readLine()) != null) {
                log.info("Sending CSV line {} to topic {}: {}", count, topic, line);
                kafkaTemplate.send(topic, line);
                count++;
            }

            log.info("Finished streaming {} CSV lines", count);

        } catch (Exception e) {
            log.error("Error while streaming CSV file", e);
        }
    }
}
