package de.weiner.dataengineering.streamingpipeline.mongo;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.index.Indexed;

import java.time.Instant;

@Document(collection = "air_quality_events")
public class AirQualityRecord {

    @Id
    private String id;

    @Indexed(unique = true)
    private String rawMessage;

    private String timestamp;
    private double temperature;
    private double humidity;
    private double co2;
    private double pm25;
    private double pm10;
    private double tvoc;
    private double co;
    private double lightIntensity;
    private boolean motionDetected;
    private int occupancyCount;
    private String ventilationStatus;

    private Instant receivedAt;

    protected AirQualityRecord() {
    }

    public AirQualityRecord(
            String rawMessage,
            String timestamp,
            double temperature,
            double humidity,
            double co2,
            double pm25,
            double pm10,
            double tvoc,
            double co,
            double lightIntensity,
            boolean motionDetected,
            int occupancyCount,
            String ventilationStatus,
            Instant receivedAt
    ) {
        this.rawMessage = rawMessage;
        this.timestamp = timestamp;
        this.temperature = temperature;
        this.humidity = humidity;
        this.co2 = co2;
        this.pm25 = pm25;
        this.pm10 = pm10;
        this.tvoc = tvoc;
        this.co = co;
        this.lightIntensity = lightIntensity;
        this.motionDetected = motionDetected;
        this.occupancyCount = occupancyCount;
        this.ventilationStatus = ventilationStatus;
        this.receivedAt = receivedAt;
    }

    public String getId() {
        return id;
    }

    public String getRawMessage() {
        return rawMessage;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public double getTemperature() {
        return temperature;
    }

    public double getHumidity() {
        return humidity;
    }

    public double getCo2() {
        return co2;
    }

    public double getPm25() {
        return pm25;
    }

    public double getPm10() {
        return pm10;
    }

    public double getTvoc() {
        return tvoc;
    }

    public double getCo() {
        return co;
    }

    public double getLightIntensity() {
        return lightIntensity;
    }

    public boolean isMotionDetected() {
        return motionDetected;
    }

    public int getOccupancyCount() {
        return occupancyCount;
    }

    public String getVentilationStatus() {
        return ventilationStatus;
    }

    public Instant getReceivedAt() {
        return receivedAt;
    }
}
